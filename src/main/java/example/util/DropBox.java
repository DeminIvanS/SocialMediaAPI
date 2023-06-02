package example.util;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.oauth.DbxRefreshResult;
import com.dropbox.core.v2.DbxClientV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class DropBox {

    private static final String DROPBOX_APP_KEY = "094kwrf5tp9irfl";
    private static final String DROPBOX_APP_SECRET = "r0zywcpfpzr181j";
    private static final String DROPBOX_PATH = "dropbox/MySocialNetwork";
    private static String refreshToken = "5Zcc-arha1vEa9O1__YbhmvxHNA17DB9UcxeRwhPlyHFW6qu3ZU";
    private String photo;

    public static String getRefreshToken(){
        try {
            DbxCredential credential = new DbxCredential(
                    "", System.currentTimeMillis(), refreshToken,DROPBOX_APP_KEY, DROPBOX_APP_SECRET);
            DbxRequestConfig config = new DbxRequestConfig(DROPBOX_APP_KEY);
            DbxRefreshResult refreshResult = credential.refresh(config);
            refreshToken = refreshResult.getAccessToken();

            DbxAppInfo appInfo = new DbxAppInfo(DROPBOX_APP_KEY, DROPBOX_APP_SECRET);
            DbxWebAuth webAuth = new DbxWebAuth(config, appInfo);
            DbxAuthFinish authFinish = webAuth.finishFromCode(refreshToken);
            log.info(authFinish.getAccessToken());
            } catch (Exception ignored) {}
            return refreshToken;
        }
    public static String dropBoxUploadImages(MultipartFile image) {
        Optional<String> originalName = Optional.ofNullable(image.getOriginalFilename());
        String imageName = "/" + UUID.randomUUID() + "." + originalName.orElse("").split("\\.")[1];

        DbxClientV2 client = getClient();
        try (InputStream in = image.getInputStream()) {
            client.files().uploadBuilder(imageName)
                    .uploadAndFinish(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageName;
    }
        public String getLinkImage(String imageName){
        try {
            photo = getClient().files().getTemporaryLink(imageName).getLink();
        }catch (Exception e){
            e.printStackTrace();
        }
        return photo;
    }
    public static DbxClientV2 getClient() {
        String token = getRefreshToken();
        DbxRequestConfig config = DbxRequestConfig.newBuilder(DROPBOX_PATH).build();
        return new DbxClientV2(config, token);
    }

}
