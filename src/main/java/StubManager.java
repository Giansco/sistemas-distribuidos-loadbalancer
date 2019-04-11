import com.google.common.collect.Iterables;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import io.grpc.StatusRuntimeException;
import product.Product;
import product.Product.ProductReply;
import product.Product.ProductRequest;
import product.ProductServiceGrpc;
import product.UserServiceGrpc;
import product.User.*;

public class StubManager {

    private Iterator<ProductServiceGrpc.ProductServiceBlockingStub> productServiceRoundRobin;
    private ProductServiceGrpc.ProductServiceBlockingStub currentProduct;
    private Iterator<ProductServiceGrpc.ProductServiceBlockingStub> inactiveProductServices;
    private ProductServiceGrpc.ProductServiceBlockingStub currentInactiveProduct;

    private Iterator<UserServiceGrpc.UserServiceBlockingStub> userServiceRoundRobin;
    private UserServiceGrpc.UserServiceBlockingStub currentUser;
    private Iterator<UserServiceGrpc.UserServiceBlockingStub> inactiveUserServices;
    private UserServiceGrpc.UserServiceBlockingStub currentInactiveUser;

    public StubManager() {
        List<UserServiceGrpc.UserServiceBlockingStub> userServiceList =  Arrays.asList(
                UserServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress("localhost", 50001).usePlaintext().build()),
                UserServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress("localhost", 50001).usePlaintext().build()),
                UserServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress("localhost", 50001).usePlaintext().build()));

        List<ProductServiceGrpc.ProductServiceBlockingStub> productServiceList =  Arrays.asList(
                ProductServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress("localhost", 50000).usePlaintext().build()),
                ProductServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress("localhost", 50000).usePlaintext().build()),
                ProductServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress("localhost", 50000).usePlaintext().build()));

        productServiceRoundRobin = Iterables.cycle(productServiceList).iterator();
        userServiceRoundRobin = Iterables.cycle(userServiceList).iterator();

        currentInactiveProduct = null;
        currentInactiveUser = null;
    }

    public ProductServiceGrpc.ProductServiceBlockingStub getNextProductService() {
        currentProduct = productServiceRoundRobin.next();
        checkProductInactivity();
        return currentProduct;
    }

    public UserServiceGrpc.UserServiceBlockingStub getNextUserService() {
        currentUser = userServiceRoundRobin.next();
        checkUserInactivity();
        return currentUser;
    }

    public void currentProductIsInactive() {
        productServiceRoundRobin.remove();

        List<ProductServiceGrpc.ProductServiceBlockingStub> inactiveProductServiceList = new ArrayList<ProductServiceGrpc.ProductServiceBlockingStub>();

        while (inactiveProductServices.hasNext()) {
            inactiveProductServiceList.add(inactiveProductServices.next());
        }

        inactiveProductServiceList.add(currentProduct);

        inactiveProductServices = Iterables.cycle(inactiveProductServiceList).iterator();
    }

    public void currentUserIsInactive() {
        userServiceRoundRobin.remove();

        List<UserServiceGrpc.UserServiceBlockingStub> inactiveUserServiceList = new ArrayList<UserServiceGrpc.UserServiceBlockingStub>();

        while (inactiveUserServices.hasNext()) {
            inactiveUserServiceList.add(inactiveUserServices.next());
        }

        inactiveUserServiceList.add(currentUser);

        inactiveUserServices = Iterables.cycle(inactiveUserServiceList).iterator();
    }

    private void inactiveProductIsNowActive() {
        inactiveProductServices.remove();

        List<ProductServiceGrpc.ProductServiceBlockingStub> productServiceList = new ArrayList<ProductServiceGrpc.ProductServiceBlockingStub>();

        while (productServiceRoundRobin.hasNext()) {
            productServiceList.add(productServiceRoundRobin.next());
        }

        productServiceList.add(currentInactiveProduct);

        if (inactiveProductServices.hasNext())
            currentInactiveProduct = inactiveProductServices.next();
        else
            currentInactiveProduct = null;

        productServiceRoundRobin = Iterables.cycle(productServiceList).iterator();
    }

    private void inactiveUserIsNowActive() {
        inactiveUserServices.remove();

        List<UserServiceGrpc.UserServiceBlockingStub> userServiceList = new ArrayList<UserServiceGrpc.UserServiceBlockingStub>();

        while (userServiceRoundRobin.hasNext()) {
            userServiceList.add(userServiceRoundRobin.next());
        }

        userServiceList.add(currentInactiveUser);

        if (inactiveUserServices.hasNext())
            currentInactiveUser = inactiveUserServices.next();
        else
            currentInactiveUser = null;

        inactiveUserServices = Iterables.cycle(userServiceList).iterator();
    }

    private void checkProductInactivity() {
        if (currentInactiveProduct != null) {
            try {
                currentInactiveProduct.isActive(Product.PingRequest.newBuilder().build());
                inactiveProductIsNowActive();
                checkProductInactivity();
            }catch (StatusRuntimeException e) {
                if (inactiveProductServices.hasNext()) {
                    currentInactiveProduct = inactiveProductServices.next();
                    checkProductInactivity();
                }
            }
        }
    }

    private void checkUserInactivity() {
        if (currentInactiveUser != null) {
            try {
                currentInactiveUser.isActive(Product.PingRequest.newBuilder().build());
                inactiveUserIsNowActive();
                checkUserInactivity();
            }catch (StatusRuntimeException e) {
                if (inactiveUserServices.hasNext()) {
                    currentInactiveUser = inactiveUserServices.next();
                    checkUserInactivity();
                }
            }
        }
    }
}
