
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import models.AddProductToWishlist;
import models.Product;
import io.grpc.StatusRuntimeException;
import org.jooby.Jooby;
import org.jooby.Request;
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
    post("/product", req -> newProduct(getModel(req, Product.class), stubManager));

    post("/user", req -> addUser(req.body().value("firstName"), req.body().value("lastName"), stubManager));
    post("/whishlist/add", req -> addProductToWishlist(getModel(req, AddProductToWishlist.class), stubManager));
    get("/whishlist/:userId", req -> getProductFromWishlist(Long.valueOf(req.param("userId").value()), stubManager));
    delete("/whishlist/:userId/:productId", req -> deleteProductFromWishlist(req.param("userId").value(), req.param("productId").value(), stubManager));

  }

  public static void main(final String[] args) {
    run(App::new, args);
  }

  public static<T> T getModel(Request req, Class<T> tClass) throws Exception{
    return new Gson().fromJson(req.body().value(), tClass);
  }

  public static ProductReply getProduct(Long id, StubManager stubManager) {
    try {
      return stubManager.getNextProductService().getProduct(ProductRequest.newBuilder().setId(id).build());
    } catch (StatusRuntimeException e) {

      switch (e.getStatus().getCode()) {
        case UNAVAILABLE:
          return getProduct(id, stubManager);
        case INTERNAL:
          throw new RuntimeException("Models.Product not found");
        default:
          throw new RuntimeException("Unknown error");
      }

    }
  }

  private static ProductReply newProduct(Product product, StubManager stubManager) {
    try {
      return stubManager.getNextProductService()
              .newProduct(NewProductRequest.newBuilder().setName(product.getName()).setDescription(product.getDescription()).build());
    }catch (StatusRuntimeException e) {
      switch (e.getStatus().getCode()) {
        case UNAVAILABLE:
          return newProduct(product, stubManager);
        case INTERNAL:
          throw new RuntimeException("Error adding product");
        default:
          throw new RuntimeException("Unknown errovaluer");
      }
    }
  }

  private static AddProductResponse addProductToWishlist(AddProductToWishlist addProductToWishlist, StubManager stubManager) {
    try {
      System.out.println("\n\nModels.Product" + addProductToWishlist.getProductId());
      System.out.println("\n\nModels.User"+ addProductToWishlist.getUserId());
      return stubManager.getNextUserService()
              .addProduct(AddProductRequest.newBuilder().setUserId(addProductToWishlist.getUserId()).setProductId(addProductToWishlist.getProductId()).build());
    }catch (StatusRuntimeException e) {
      switch (e.getStatus().getCode()) {
        case UNAVAILABLE:
          return addProductToWishlist(addProductToWishlist, stubManager);
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
          throw new RuntimeException("Models.User not found");
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

  private static AddUserResponse addUser(String firstName, String lastName, StubManager stubManager) {
    try {
      return stubManager.getNextUserService().addUser(AddUserRequest.newBuilder().setFirstName(firstName).setLastName(lastName).build());
    }catch (StatusRuntimeException e) {
      switch (e.getStatus().getCode()) {
        case UNAVAILABLE:
          return addUser(firstName, firstName, stubManager);
        case INTERNAL:
          throw new RuntimeException("Error adding an user");
        default:
          throw new RuntimeException("Unknown error");
      }
    }
  }
}

