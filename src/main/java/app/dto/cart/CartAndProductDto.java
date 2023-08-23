package app.dto.cart;


import app.entity.Cart;
import app.entity.Product;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;


@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CartAndProductDto {
    private Long cartProductQuantity;
    private Long productId;
    private Long categoryId;
    private String name;
    private String description;
    private Long price;
    private Long productQuantity;
    private String code;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
