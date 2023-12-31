package app.service.member;

import app.dao.encryption.EncryptionDao;
import app.dao.member.MemberDao;
import app.dto.member.request.LoginDto;
import app.dto.member.request.MemberRegisterDto;
import app.dto.member.response.MemberDetail;
import app.entity.Encryption;
import app.entity.Member;
import app.exception.member.DuplicatedEmailException;
import app.exception.member.LoginFailException;
import app.exception.member.MemberEntityNotFoundException;
import app.exception.member.RegisterException;
import app.utils.CipherUtil;
import app.utils.GetSessionFactory;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Optional;

public class MemberService {

  private final MemberDao memberDao;
  private final EncryptionDao encryptionDao;
  private final SqlSessionFactory sessionFactory;

  public MemberService() {
    memberDao = new MemberDao();
    encryptionDao = new EncryptionDao();
    sessionFactory = GetSessionFactory.getInstance();
  }

  public Boolean register(MemberRegisterDto dto) {
    SqlSession sqlSession = sessionFactory.openSession();
    int result = 0;
    try {
      String salt = createSalt();
      String hashedPassword = createHashedPassword(dto.getPassword(), salt);

      Member member = dto.toEntity(hashedPassword);
      memberDao.insert(member, sqlSession);

      Encryption encryption = Encryption.from(member, salt);
      result = encryptionDao.insert(encryption, sqlSession);

      sqlSession.commit();

    } catch (PersistenceException e) {
      sqlSession.rollback();
      throw new DuplicatedEmailException();
    } catch (Exception e) {
      sqlSession.rollback();
      throw new RegisterException();
    } finally {
      sqlSession.close();
    }
    return result == 1 ? true : false;
  }

  public MemberDetail login(LoginDto dto) throws Exception {
    SqlSession sqlSession = sessionFactory.openSession();
    MemberDetail loginMember = null;
    try {
      String hashedPassword = getHashedPassword(dto, sqlSession);
      dto.setPassword(hashedPassword);
      Member member =
          memberDao.selectByEmailAndPassword(dto, sqlSession).orElseThrow(LoginFailException::new);

      loginMember = MemberDetail.of(member);

    } catch (SQLException e) {

    } finally {
      sqlSession.close();
    }
    return loginMember;
  }

  public MemberDetail kakaoLogin(MemberRegisterDto dto) {
    SqlSession sqlSession = sessionFactory.openSession();
    MemberDetail loginMember = null;
    Member member = null;
    try {
      Optional<Member> optionalMember = memberDao.selectByEmail(dto.getEmail(), sqlSession);
      if (optionalMember.isEmpty()) {

        member = dto.toEntity("ASDQWEASDQWEASD");
        memberDao.insert(member, sqlSession);
        sqlSession.commit();
        loginMember = MemberDetail.of(member);
      } else {
        member = optionalMember.get();
        loginMember = MemberDetail.of(member);
      }

    } catch (SQLException e) {
      sqlSession.rollback();
    } finally {
      sqlSession.close();
    }
    return loginMember;
  }

  public MemberDetail get(Long id) {
    SqlSession sqlSession = sessionFactory.openSession();
    MemberDetail memberDetail = null;
    try {
      Member member =
          memberDao.selectById(id, sqlSession).orElseThrow(MemberEntityNotFoundException::new);
      memberDetail = MemberDetail.of(member);
    } catch (SQLException e) {
    } finally {
      sqlSession.close();
    }
    return memberDetail;
  }

  public Boolean isDuplicatedEmail(String email) {
    SqlSession sqlSession = sessionFactory.openSession();
    int result = 0;
    try {
      result = memberDao.countByEmail(email, sqlSession);
    } catch (SQLException e) {
    } finally {
      sqlSession.close();
    }
    return result == 0 ? true : false;
  }

  private String getHashedPassword(LoginDto dto, SqlSession sqlSession) throws Exception {
    Encryption encryption =
        encryptionDao
            .selectByEmail(dto.getEmail(), sqlSession)
            .orElseThrow(MemberEntityNotFoundException::new);
    String hashedPassword = createHashedPassword(dto.getPassword(), encryption.getSalt());
    return hashedPassword;
  }

  private String createHashedPassword(String password, String salt) throws Exception {
    new String();
    CipherUtil.getSHA256(password, salt);
    return new String(CipherUtil.getSHA256(password, salt)).replaceAll(" ", "");
  }

  private String createSalt() throws NoSuchAlgorithmException {
    byte[] key = CipherUtil.generateKey("AES", 128);
    String salt = CipherUtil.byteArrayToHex(key);
    return salt;
  }
}
