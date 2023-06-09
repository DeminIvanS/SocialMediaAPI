package example.service;

import example.handler.exception.EntityNotFoundException;
import example.model.dto.response.ListResponse;
import example.model.dto.response.PersonResponse;
import example.model.entity.Person;
import example.model.enums.FriendshipStatusCode;
import example.repository.PersonRepository;
import example.security.jwt.JwtTokenProvider;
import example.util.PhotoCloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FriendsService {
    private final PersonService personService;
    private final FriendshipService friendshipService;
    private final PersonRepository personRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PhotoCloudinary photoCloudinary;

    public ListResponse<PersonResponse> getRecommendations(String token, int offset, int itemPerPage) {

        Person person = personRepository.findByEmail(jwtTokenProvider.getUsername(token));
        Integer myId = person.getId();

        List<Integer> myFriendsIds = getMyFriendsIds(myId);
        List<Integer> friendsIdsForFriends = getFriendsIdsForFriends(myFriendsIds, myId);
        Map<Integer, Integer> recommendationsCounts = getRecommendationsCounts(friendsIdsForFriends);
        List<Integer> sortedRecommendationsByCount = getSortedRecommendationsByCount(recommendationsCounts);
        List<Integer> filteredFriendsIds = filterFriendsIds(sortedRecommendationsByCount, myId);
        List<Integer> limitedRecommendations = limitRecommendations(filteredFriendsIds, offset, itemPerPage);
        List<Person> persons = getPersons(limitedRecommendations);

        return getResultJson(persons, friendsIdsForFriends.size(), offset, itemPerPage);
    }

    private List<Integer> filterFriendsIds(List<Integer> list, int myId) {
        List<Integer> result = new ArrayList<>();
        for (Integer dstId : list) {
            var friendship = friendshipService.findByFriendShip(myId, dstId);
            if (friendship.isEmpty()) {
                result.add(dstId);
            }
        }
        return result;
    }

    private List<Integer> getMyFriendsIds(Integer myId) {

        return friendshipService
                .findByPersonIdAndStatus(myId, FriendshipStatusCode.FRIEND).stream()
                .flatMap(friendship -> Stream.of(
                        friendship.getSrcPersonId(), friendship.getDstPersonId()
                ))
                .filter(id -> !id.equals(myId))
                .collect(Collectors.toList());
    }

    private List<Integer> getFriendsIdsForFriends(List<Integer> friendsIds, Integer myId) {

        return friendsIds.stream()
                .flatMap(id -> friendshipService
                        .findByPersonIdAndStatus(id, FriendshipStatusCode.FRIEND).stream()
                        .flatMap(fs -> Stream.of(fs.getSrcPersonId(), fs.getDstPersonId()))
                        .filter(personId -> !friendsIds.contains(personId))
                        .filter(personId -> !personId.equals(myId)))
                .collect(Collectors.toList());
    }

    private Map<Integer, Integer> getRecommendationsCounts(List<Integer> friendsIdsForFriends) {

        Map<Integer, Integer> friendshipsCount = new HashMap<>();

        friendsIdsForFriends.forEach(id -> {
            Integer count = friendshipsCount.getOrDefault(id, 0);
            friendshipsCount.put(id, count + 1);
        });

        return friendshipsCount;
    }

    private List<Integer> getSortedRecommendationsByCount(Map<Integer, Integer> recommendationsCounts) {

        return recommendationsCounts.keySet().stream()
                .sorted((ffc1, ffc2) ->
                        recommendationsCounts.get(ffc2).compareTo(recommendationsCounts.get(ffc1)))
                .collect(Collectors.toList());
    }

    private List<Integer> limitRecommendations(List<Integer> sortedRecommendationsByCount,
                                               int offset, int itemPerPage) {
        return sortedRecommendationsByCount.stream()
                .skip(offset)
                .limit(itemPerPage)
                .collect(Collectors.toList());
    }

    private List<Person> getPersons(List<Integer> limitedRecommendations) {

        return limitedRecommendations.stream()
                .map(id -> {
                    try {
                        return Optional.of(personRepository.findNotDeletedById(id));
                    } catch (EntityNotFoundException e) {
                        return Optional.empty();
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Person.class::cast)
                .collect(Collectors.toList());
    }

    private ListResponse<PersonResponse> getResultJson(List<Person> persons, Integer total,
                                                   Integer offset, Integer itemPerPage) {

        List<PersonResponse> data = persons.stream()
                .map(person -> PersonResponse.builder()
                        .id(person.getId())
                        .firstName(person.getFirstName())
                        .lastName(person.getLastName())
                        .regDate(person.getRegDate())
                        .birthDate(person.getBirthDate())
                        .email(person.getEmail())
                        .phone(person.getPhone())
                        .photo(photoCloudinary.getUrl(person.getId()))
                        .about(person.getAbout())
                        .city(person.getCity())
                        .country(person.getCountry())
                        .messagePermission(person.getMessagePermission())
                        .lastOnlineTime(person.getLastOnlineTime())
                        .isBlocked(person.getIsBlocked())
                        .friendshipStatusCode(personService.getFriendshipStatus(person.getId()))
                        .online(Objects.equals(person.getOnlineStatus(), "ONLINE"))
                        .build())
                .collect(Collectors.toList());

        return ListResponse.<PersonResponse>builder()
                .error("")
                .timestamp(System.currentTimeMillis())
                .total((total == 0) ? data.size() : total)
                .offset(offset)
                .perPage(itemPerPage)
                .data(data)
                .build();
    }

    public ListResponse<PersonResponse> getListFriends(Integer offset, Integer itemPerPage) {
        var person = personService.getAuthorizedPerson();
        List<Person> personList = personRepository.getFriendsPersonById(person.getId());
        List<Person> result = new ArrayList<>();
        for (Person p : personList) {
            if (!Objects.equals(p.getId(), person.getId())) {
                result.add(p);
            }
        }
        return getResultJson(result, 0, offset, itemPerPage);
    }

    public ListResponse<PersonResponse> getListApplicationsFriends(Integer offset, Integer itemPerPage) {

        List<Person> personList = personRepository.getApplicationsFriendsPersonById(personService.getAuthorizedPerson().getId());
        return getResultJson(personList, 0, offset, itemPerPage);
    }
        }
