import com.google.common.collect.Iterables;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.jooby.Jooby;
import proto.Product;
import proto.Product.ProductReply;
import proto.Product.ProductRequest;
import proto.ProductServiceGrpc;
import proto.UserServiceGrpc;
import proto.User.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

/**
 * @author jooby generator
 */
public class App extends Jooby {

  private List<ProductServiceGrpc.ProductServiceBlockingStub> productServiceList =  Arrays.asList(
          ProductServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress("172.22.45.147", 50000).usePlaintext().build()),
          ProductServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress("172.22.45.147", 50000).usePlaintext().build()),
          ProductServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress("172.22.45.147", 50000).usePlaintext().build()));

  private List<UserServiceGrpc.UserServiceBlockingStub> userServiceList =  Arrays.asList(
          UserServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress("172.22.45.147", 50000).usePlaintext().build()),
          UserServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress("10.0.0.55", 50000).usePlaintext().build()),
          UserServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress("10.0.0.56", 50000).usePlaintext().build()));

//  private final LoadBalancerClient loadBalancerClient = new LoadBalancerClient("localhost",3000);

  private final Iterator<ProductServiceGrpc.ProductServiceBlockingStub> productServiceRoundRobin = Iterables.cycle(productServiceList).iterator();
  private final Iterator<UserServiceGrpc.UserServiceBlockingStub> userServiceRoundRobin = Iterables.cycle(userServiceList).iterator();

  StreamObserver<ProductReply> responseObserver = new StreamObserver<ProductReply>() {
    @Override
    public void onNext(ProductReply summary) {
      System.out.println("Finished trip wit"+ summary.getName());
    }

    @Override
    public void onError(Throwable t) {
      Status status = Status.fromThrowable(t);
      System.out.println("Finished trip wit"+ status.getDescription());

    }

    @Override
    public void onCompleted() {
      System.out.println("Finished ProductReply");
    }
  };


  {
    get("/", () -> "Hello World!");

    get("/product/:id", req -> productServiceRoundRobin.next().getProduct(ProductRequest.newBuilder().setId(Long.valueOf(req.param("id").value())).build()));

//    get("/whishlist/add", (req, rsp) -> rsp.send(userServiceRoundRobin.next().addProduct()))
  }

  public static void main(final String[] args) {
    run(App::new, args);
  }



}
