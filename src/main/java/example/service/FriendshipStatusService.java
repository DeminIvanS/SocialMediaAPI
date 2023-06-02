package example.service;

import example.model.dto.response.ComplexResponse;
import example.model.dto.response.FriendshipResponse;
import example.model.entity.Friendship;
import example.model.entity.FriendshipStatus;
import example.model.enums.FriendshipStatusCode;
import example.repository.FriendshipStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipStatusService {
    private final FriendshipStatusRepository friendshipStatusRepository;

    public FriendshipStatus findById(int id) {
        return friendshipStatusRepository.findById(id);
    }

    public int addRequestStatus() {
        LocalDateTime localDateTime = LocalDateTime.now();

        FriendshipStatus friendshipStatus = new FriendshipStatus();
        friendshipStatus.setTime(localDateTime);
        friendshipStatus.setCode(FriendshipStatusCode.REQUEST);
        return friendshipStatusRepository.save(friendshipStatus);
    }

    public int addReceivedRequestStatus() {
        LocalDateTime localDateTime = LocalDateTime.now();

        FriendshipStatus friendshipStatus = new FriendshipStatus();
        friendshipStatus.setTime(localDateTime);
        friendshipStatus.setCode(FriendshipStatusCode.RECEIVED_REQUEST);
        return friendshipStatusRepository.save(friendshipStatus);
    }

    public FriendshipResponse updateStatus(Integer srcPersonId, Integer id, FriendshipStatusCode friendshipStatusCode) {

        List<FriendshipStatus> friendshipStatusList = friendshipStatusRepository.getApplicationsFriendshipStatus(srcPersonId, id);

        for (FriendshipStatus friendshipStatus : friendshipStatusList) {
            friendshipStatusRepository.updateCode(friendshipStatus.getId(), friendshipStatusCode);
        }

        var data = ComplexResponse.builder().message("ok").build();
        LocalDateTime localDateTime = LocalDateTime.now();

        return new FriendshipResponse("", localDateTime, data);
    }


    public FriendshipResponse deleteStatus(List<Friendship> friendshipList) {

        for (Friendship friendship : friendshipList) {
            FriendshipStatus friendshipStatus = new FriendshipStatus();
            friendshipStatus.setId(friendship.getStatusId());
            friendshipStatusRepository.delete(friendshipStatus);
        }
        LocalDateTime localDateTime = LocalDateTime.now();
        var data = ComplexResponse.builder().message("ok").build();

        return new FriendshipResponse("", localDateTime, data);

    }
}
