package example.repository;

import example.handler.exception.ErrorException;
import example.mapper.CaptchaMapper;
import example.model.entity.Captcha;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CaptchaRepository {
    private final JdbcTemplate jdbcTemplate;

    public int addCaptcha(long time, String code, String secretCode){
        try {
            if(jdbcTemplate.queryForObject("SELECT MAX(id) FROM captcha", Integer.class) == null) {
                jdbcTemplate.update("INSERT INTO captcha (id, time, code, secret_code)" +
                        "VALUES (?,?,?,?)", 1, new Timestamp(time), code, secretCode);
                return 1;
            }
            int id = jdbcTemplate.queryForObject("SELECT MAX(id) FROM captcha", Integer.class) + 1;
            jdbcTemplate.update("INSERT INTO captcha (id, time, code, secret_code)" +
                    "VALUES (?,?,?,?)", 1, new Timestamp(time), code, secretCode);
            return id;
        } catch (DataAccessException e){
            return -1;
        }
    }
    public Captcha findByCode(String code){
        Captcha captcha;
        captcha = jdbcTemplate.queryForObject("SELECT * FROM captcha WHERE code = ?", new Object[]{code},
                new CaptchaMapper());
        return captcha;
    }
    public List<Captcha> findAll() {
        List<Captcha> repeatlist;
        try {
            repeatlist = jdbcTemplate.query("SELECT * FROM captcha", new CaptchaMapper());
        }catch (DataAccessException e) {
            throw new ErrorException(e.getMessage());
        }
        return repeatlist;
    }
    public boolean deleteCaptcha(Captcha captcha){
        boolean repeatValue;
        try {
            repeatValue = (jdbcTemplate.update("DELETE FROM captcha WHERE id = ?", captcha.getId()) == 1);
        }catch (DataAccessException e){
            throw new ErrorException(e.getMessage());
        }
        return repeatValue;
    }
    public void save(Captcha captcha) {
        String sql = "INSERT INTO captcha(time, code, secret_code) values(?,?,?) ";
        jdbcTemplate.update(sql, captcha.getTime(),captcha.getCode(), captcha.getSecretCode());
    }
}
