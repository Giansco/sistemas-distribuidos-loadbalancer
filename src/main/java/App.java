import com.google.common.collect.Iterables;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import javassist.NotFoundException;
import org.jooby.Jooby;
import product.Product;
import product.Product.ProductReply;
import product.Product.ProductRequest;
import product.Product.NewProductRequest;
import product.ProductServiceGrpc;
import product.UserServiceGrpc;
import product.User.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

/**
 * @author jooby generator
 */
public class App extends Jooby {

  StubManager stubManager = new StubManager();


  {
    get("/", () -> "Hello World!");

    get("/product/:id", req -> getProduct(Long.valueOf(req.param("id").value()), stubManager));
    post("/product", req -> newProduct(req.body().value("name"), req.body().value("description"), stubManager));
//    get("/whishlist/add", (req, rsp) -> rsp.send(userServiceRoundRobin.next().addProduct()))
  }

  public static void main(final String[] args) {
    run(App::new, args);
  }

  public static ProductReply getProduct(Long id, StubManager stubManager) {
    try {
      return stubManager.getNextProductService().getProduct(ProductRequest.newBuilder().setId(id).build());
    } catch (StatusRuntimeException e) {

      switch (e.getStatus().getCode()) {
        case UNAVAILABLE:
          return getProduct(id, stubManager);
        case INTERNAL:
          throw new RuntimeException("Product not found");
        default:
          throw new RuntimeException("Unknown error");
      }

    }
  }

  public static ProductReply newProduct(String name, String description, StubManager stubManager) {
    return stubManager.getNextProductService().newProduct(NewProductRequest.newBuilder().setName(name).setDescription(description).build());
  }
}

