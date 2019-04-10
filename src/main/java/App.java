
import io.grpc.StatusRuntimeException;
import org.jooby.Jooby;
import product.Product.ProductReply;
import product.Product.ProductRequest;
import product.Product.NewProductRequest;
import product.User.*;

/**
 * @author jooby generator
 */
public class App extends Jooby {

  StubManager stubManager = new StubManager();


  {
    get("/", () -> "Hello World!");

    get("/product/:id", req -> getProduct(Long.valueOf(req.param("id").value()), stubManager));
    post("/product", req -> newProduct(req.body().value("name"), req.body().value("description"), stubManager));
    post("/whishlist/add", req -> addProductToWishlist(req.body().value("userId"), req.body().value("productId"), stubManager));
    get("/whishlist/:id", req -> getProductFromWishlist(Long.valueOf(req.param("id").value()), stubManager));
    delete("/whishlist/:userId/:productId", req -> deleteProductFromWishlist(req.param("userId").value(), req.param("productId").value(), stubManager));

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

  private static ProductReply newProduct(String name, String description, StubManager stubManager) {
    try {
      return stubManager.getNextProductService().newProduct(NewProductRequest.newBuilder().setName(name).setDescription(description).build());
    }catch (StatusRuntimeException e) {
      switch (e.getStatus().getCode()) {
        case UNAVAILABLE:
          return newProduct(name, description, stubManager);
        case INTERNAL:
          throw new RuntimeException("Error adding product");
        default:
          throw new RuntimeException("Unknown error");
      }
    }
  }

  private static AddProductResponse addProductToWishlist(String userId, String productId, StubManager stubManager) {
    try {
      return stubManager.getNextUserService().addProduct(AddProductRequest.newBuilder().setUserId(Long.valueOf(userId)).setProductId(Long.valueOf(productId)).build());
    }catch (StatusRuntimeException e) {
      switch (e.getStatus().getCode()) {
        case UNAVAILABLE:
          return addProductToWishlist(userId, productId, stubManager);
        case INTERNAL:
          throw new RuntimeException("Error adding product to wishlist");
        default:
          throw new RuntimeException("Unknown error");
      }
    }
  }

  private static GetProductsResponse getProductFromWishlist(Long id, StubManager stubManager) {
    try {
      return stubManager.getNextUserService().getProducts(GetProductsRequest.newBuilder().setUserId(id).build());
    } catch (StatusRuntimeException e) {

      switch (e.getStatus().getCode()) {
        case UNAVAILABLE:
          return getProductFromWishlist(id, stubManager);
        case INTERNAL:
          throw new RuntimeException("User not found");
        default:
          throw new RuntimeException("Unknown error");
      }
    }
  }

  private static DeleteProductResponse deleteProductFromWishlist(String userId, String productId, StubManager stubManager) {
      try {
        return stubManager.getNextUserService().deleteProduct(DeleteProductRequest.newBuilder().setUserId(Long.valueOf(userId)).setProductId(Long.valueOf(productId)).build());
      }catch (StatusRuntimeException e) {

        switch (e.getStatus().getCode()) {
          case UNAVAILABLE:
            return deleteProductFromWishlist(userId, productId, stubManager);
          case INTERNAL:
            throw new RuntimeException("Error deleting product from wishlist");
          default:
            throw new RuntimeException("Unknown error");
        }
      }
  }
}

