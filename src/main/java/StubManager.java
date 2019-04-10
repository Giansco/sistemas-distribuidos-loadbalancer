import com.google.common.collect.Iterables;
import io.grpc.ManagedChannelBuilder;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import product.Product;
import product.Product.ProductReply;
import product.Product.ProductRequest;
import product.ProductServiceGrpc;
import product.UserServiceGrpc;
import product.User.*;

public class StubManager {

    private final Iterator<ProductServiceGrpc.ProductServiceBlockingStub> productServiceRoundRobin;

    private final Iterator<UserServiceGrpc.UserServiceBlockingStub> userServiceRoundRobin;

    public StubManager() {
        List<UserServiceGrpc.UserServiceBlockingStub> userServiceList =  Arrays.asList(
                UserServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress("localhost", 50000).usePlaintext().build()),
                UserServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress("localhost", 50000).usePlaintext().build()),
                UserServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress("localhost", 50000).usePlaintext().build()));

        List<ProductServiceGrpc.ProductServiceBlockingStub> productServiceList =  Arrays.asList(
                ProductServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress("localhost", 50000).usePlaintext().build()),
                ProductServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress("localhost", 50000).usePlaintext().build()),
                ProductServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress("localhost", 50000).usePlaintext().build()));

        productServiceRoundRobin = Iterables.cycle(productServiceList).iterator();
        userServiceRoundRobin = Iterables.cycle(userServiceList).iterator();
    }

    public ProductServiceGrpc.ProductServiceBlockingStub getNextProductService() {
        return productServiceRoundRobin.next();
    }
}
