package app.service.cart;

import app.dto.cart.AllCartProductInfoDto;
import app.dto.cart.CartProductDto;
import app.dto.comp.ProductAndMemberCompositeKey;
import app.dto.product.ProductItemQuantity;
import app.entity.Cart;
import app.error.ErrorCode;
import java.util.List;

public interface CartService {

  AllCartProductInfoDto getCartProductListByMember(ProductAndMemberCompositeKey productAndMemberCompositeKeys)
      throws Exception;

  ErrorCode putItemIntoCart(ProductAndMemberCompositeKey productAndMemberCompositeKey,
      Integer quantity)
      throws Exception;

  ErrorCode updateQuantityOfCartProduct(ProductAndMemberCompositeKey productAndMemberCompositeKey,
      Long productId, Integer updateQuantity) throws Exception;

  ErrorCode delete(ProductAndMemberCompositeKey productAndMemberCompositeKeyList) throws Exception;


}