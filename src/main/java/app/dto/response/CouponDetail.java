package app.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CouponDetail {
  private String name;
  private String discountPolicy;
  private String discountValue;
  private String status;
}
