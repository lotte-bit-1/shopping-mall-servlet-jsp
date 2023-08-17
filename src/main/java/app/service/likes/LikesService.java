package app.service.likes;

import app.dto.comp.ProductAndMemberCompositeKey;
import app.dto.likes.response.MemberLikesResponseDto;
import java.util.List;

public interface LikesService {
  List<MemberLikesResponseDto> getMemberLikes(Long memberId);
  boolean getMemberProductLikes(ProductAndMemberCompositeKey productAndMemberCompositeKey);
  int addLikes(ProductAndMemberCompositeKey productAndMemberCompositeKey);
  int removeLikes(ProductAndMemberCompositeKey productAndMemberCompositeKey);
}
