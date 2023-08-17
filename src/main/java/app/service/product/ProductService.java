package app.service.product;

import app.dto.product.ProductListItem;
import java.util.List;

public interface ProductService {
  List<ProductListItem> getProductsByLowerPrice() throws Exception;

  List<ProductListItem> getProductsByHigherPrice();

  List<ProductListItem> getProductsByDate();
}