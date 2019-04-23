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

class StubManager {

    private  ServiceManager serviceManager;

    StubManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    ProductServiceGrpc.ProductServiceBlockingStub getNextProductService() {
        AddressWithPort address = serviceManager.getAddress("product");
        return ProductServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress(address.getAddress(), address.getPort()).usePlaintext().build());
    }

    UserServiceGrpc.UserServiceBlockingStub getNextUserService() {
        AddressWithPort address = serviceManager.getAddress("wishlist");
        return UserServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress(address.getAddress(), address.getPort()).usePlaintext().build());
    }


}
