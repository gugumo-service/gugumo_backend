package sideproject.gugumo.api.swaggerapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sideproject.gugumo.cond.SortType;
import sideproject.gugumo.domain.dto.detailpostdto.DetailPostDto;
import sideproject.gugumo.domain.dto.memberDto.CustomUserDetails;
import sideproject.gugumo.domain.dto.simplepostdto.SimplePostDto;
import sideproject.gugumo.domain.entity.meeting.GameType;
import sideproject.gugumo.domain.entity.meeting.Location;
import sideproject.gugumo.page.PageCustom;
import sideproject.gugumo.request.CreatePostReq;
import sideproject.gugumo.request.UpdatePostReq;
import sideproject.gugumo.response.ApiResponse;
import sideproject.gugumo.swagger.detailpostdtoresponse.LongDetailPostDtoResponse;
import sideproject.gugumo.swagger.detailpostdtoresponse.ShortDetailPostDtoResponse;
import sideproject.gugumo.swagger.simplepostdtoresponse.SimplePostLongDtoResponse;
import sideproject.gugumo.swagger.simplepostdtoresponse.SimplePostShortDtoResponse;

import java.util.List;

public interface PostApi {

    @Operation(summary = "글쓰기", description = "게시글을 작성합니다.",
                responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "글 작성 완료",
                                                                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                                                                     examples = @ExampleObject(
                                                                             value = "{\"status\" : \"success\", \"data\" : \"글 작성 완료\", \"message\" : null}"
                                                                     ))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "게시글 저장 권한 없음",
                                                                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                                                                     examples = @ExampleObject(
                                                                             value = "{\"status\" : \"fail\", \"data\" : null, \"message\" : \"저장 실패: 게시글 저장 권한이 없습니다.\"}"
                                                                     )))
                })
    ApiResponse<String> save(@AuthenticationPrincipal CustomUserDetails principal,
                             @RequestBody @Valid CreatePostReq createPostReq);


    @Operation(summary = "게시글 조회", description = "주어진 조건에 맞는 게시글을 조회합니다.",
                responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 정보",
                                                                    content=@Content(mediaType = "application/json", schema = @Schema(oneOf = {SimplePostShortDtoResponse.class, SimplePostLongDtoResponse.class}),
                                                                    examples = @ExampleObject(value = """
                                                                            {
                                                                                "status": "success",
                                                                                   "data": {
                                                                                    "content": [
                                                                                        {
                                                                                            "postId": 4,
                                                                                            "meetingStatus": "RECRUIT",
                                                                                            "gameType": "BASKETBALL",
                                                                                            "location": "SEOUL",
                                                                                            "title": "test1",
                                                                                            "meetingMemberNum": 3,
                                                                                            "meetingDeadline": "2024-12-11",
                                                                                            "meetingDateTime": "2024-12-21T15:00:00",
                                                                                            "bookmarked": false
                                                                                        },
                                                                                        {
                                                                                            "postId": 3,
                                                                                            "meetingStatus": "RECRUIT",
                                                                                            "gameType": "BASKETBALL",
                                                                                            "location": "SEOUL",
                                                                                            "title": "test1",
                                                                                            "meetingMemberNum": 3,
                                                                                            "meetingDeadline": "2024-12-11",
                                                                                            "meetingDateTime": "2024-12-21T15:00:00",
                                                                                            "bookmarked": false
                                                                                        },
                                                                                        {
                                                                                            "postId": 2,
                                                                                            "meetingStatus": "RECRUIT",
                                                                                            "gameType": "BASKETBALL",
                                                                                            "location": "SEOUL",
                                                                                            "title": "test1",
                                                                                            "meetingMemberNum": 3,
                                                                                            "meetingDeadline": "2024-12-11",
                                                                                            "meetingTime": "15:00:00",
                                                                                            "meetingDays": "MON;WED;FRI",
                                                                                            "bookmarked": false
                                                                                        },
                                                                                        {
                                                                                            "postId": 1,
                                                                                            "meetingStatus": "RECRUIT",
                                                                                            "gameType": "BASKETBALL",
                                                                                            "location": "SEOUL",
                                                                                            "title": "test1",
                                                                                            "meetingMemberNum": 3,
                                                                                            "meetingDeadline": "2024-12-11",
                                                                                            "meetingTime": "15:00:00",
                                                                                            "meetingDays": "MON;WED;FRI",
                                                                                            "bookmarked": false
                                                                                        }
                                                                                    ],
                                                                                    "pageableCustom": {
                                                                                        "number": 1,
                                                                                        "size": 12,
                                                                                        "sort": {
                                                                                            "empty": true,
                                                                                            "sorted": false,
                                                                                            "unsorted": true
                                                                                        },
                                                                                        "first": true,
                                                                                        "last": true,
                                                                                        "hasNext": false,
                                                                                        "totalPages": 1,
                                                                                        "totalElements": 4,
                                                                                        "numberOfElements": 4,
                                                                                        "empty": false
                                                                                    }
                                                                                },
                                                                                "message": null
                                                                            }
                                                                            """)))
                })
    <T extends SimplePostDto> ApiResponse<PageCustom<T>> findPostSimple(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PageableDefault(size = 12) @Parameter(hidden = true) Pageable pageable,
            @RequestParam(required = false, value = "q") @Parameter(description = "검색어") String q,
            @RequestParam(required = false, value = "location") @Parameter(description = "모임 지역", schema = @Schema(implementation = Location.class)) String location,
            @RequestParam(required = false, value = "gametype") @Parameter(description = "종목", schema = @Schema(implementation = GameType.class)) String gameType,
            @RequestParam(required = false, value = "meetingstatus", defaultValue = "RECRUIT") @Parameter(description = "종목", schema = @Schema(allowableValues = {"RECRUIT, END, ALL"})) String meetingStatus,
            @RequestParam(required = false, value = "sort", defaultValue = "NEW") @Parameter(description = "정렬 조건", schema = @Schema(implementation = SortType.class)) String sortType);


    @Operation(summary = "게시글 상세 조회", description = "게시글의 상세 정보를 조회합니다.",
            responses = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "글 상세 내용",
                        content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {ShortDetailPostDtoResponse.class, LongDetailPostDtoResponse.class}),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "status": "success",
                                                    "data": {
                                                        "postId": 2,
                                                        "author": "testnick",
                                                        "meetingType": "SHORT",
                                                        "gameType": "BADMINTON",
                                                        "meetingMemberNum": 1,
                                                        "meetingDeadline": "2024-05-06",
                                                        "openKakao": "open",
                                                        "location": "GYEONGGI",
                                                        "title": "테스트 asdf제목입니다.",
                                                        "content": "테스트 내용입니다.",
                                                        "createdDateTime": "2024-05-21T20:09:47.373269",
                                                        "meetingStatus": "RECRUIT",
                                                        "viewCount": 1,
                                                        "bookmarkCount": 0,
                                                        "meetingDateTime": "2024-05-06T15:00:00",
                                                        "bookmarked": false,
                                                        "authorExpired": false,
                                                        "yours": true
                                                    },
                                                    "message": null
                                                }
                                                """
                                ))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "권한 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            value = "{\"status\" : \"fail\", \"data\" : null, \"message\" : \"조회 실패: 권한이 없습니다.\"}"
                                    ))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글이 존재하지 않음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            value = "{\"status\" : \"fail\", \"data\" : null, \"message\" : \"조회 실패: 해당 게시글이 존재하지 않습니다.\"}"
                                    )))

            })
    <T extends DetailPostDto> ApiResponse<T> findPostDetail(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable("post_id") @Parameter(description = "조회할 게시글 고유번호") Long postId);


    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "글 갱신 완료",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            value = "{\"status\" : \"success\", \"data\" : \"글 갱신 완료\", \"message\" : null}"
                                    ))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                                    examples = {
                                    @ExampleObject(name = "비로그인 사용자",
                                                    value = "{\"status\" : \"fail\", \"data\" : null, \"message\" : \"수정 실패: 비로그인 사용자입니다.\"}"
                                            ),
                                    @ExampleObject(name = "수정 권한 없음",
                                            value = "{\"status\" : \"fail\", \"data\" : null, \"message\" : \"수정 실패: 게시글 수정 권한이 없습니다.\"}"
                                    )

                                    })),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글이 존재하지 않음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            value = "{\"status\" : \"fail\", \"data\" : null, \"message\" : \"수정 실패: 해당 게시글이 존재하지 않습니다.\"}"
                                    )))

    })
    ApiResponse<String> updatePost(@AuthenticationPrincipal CustomUserDetails principal,
                                   @PathVariable("post_id") @Parameter(description = "수정할 게시글 고유번호") Long postId,
                                   @RequestBody @Valid UpdatePostReq updatePostReq);


    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "글 삭제 완료",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            value = "{\"status\" : \"success\", \"data\" : \"글 삭제 완료\", \"message\" : null}"
                                    ))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                                    examples = {
                                    @ExampleObject(name = "비로그인 사용자",
                                                    value = "{\"status\" : \"fail\", \"data\" : null, \"message\" : \"삭제 실패: 비로그인 사용자입니다.\"}"
                                            ),
                                    @ExampleObject(name = "삭제 권한 없음",
                                            value = "{\"status\" : \"fail\", \"data\" : null, \"message\" : \"삭제 실패: 게시글 삭제 권한이 없습니다.\"}"
                                    )
                                    })),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글이 존재하지 않음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            value = "{\"status\" : \"fail\", \"data\" : null, \"message\" : \"삭제 실패: 해당 게시글이 존재하지 않습니다.\"}"
                                    )))
            })
    ApiResponse<String> deletePost(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable("post_id") Long postId);


    @Operation(summary = "유저 게시글 조회", description = "내가 작성한 게시글을 조회합니다.",
            responses = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "유저 게시글 리스트",
                        content=@Content(mediaType = "application/json", schema = @Schema(oneOf = {SimplePostShortDtoResponse.class, SimplePostLongDtoResponse.class}),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "status": "success",
                                                "data": {
                                                    "content": [
                                                        {
                                                            "postId": 2,
                                                            "meetingStatus": "RECRUIT",
                                                            "gameType": "BASKETBALL",
                                                            "location": "SEOUL",
                                                            "title": "test21",
                                                            "meetingMemberNum": 3,
                                                            "meetingDeadline": "2024-12-11",
                                                            "meetingTime": "15:00:00",
                                                            "meetingDays": "MON;WED;FRI",
                                                            "bookmarked": false
                                                        },
                                                        {
                                                            "postId": 1,
                                                            "meetingStatus": "RECRUIT",
                                                            "gameType": "BASKETBALL",
                                                            "location": "SEOUL",
                                                            "title": "test21",
                                                            "meetingMemberNum": 3,
                                                            "meetingDeadline": "2024-12-11",
                                                            "meetingTime": "15:00:00",
                                                            "meetingDays": "MON;WED;FRI",
                                                            "bookmarked": false
                                                        }
                                                    ],
                                                    "pageable": {
                                                        "number": 1,
                                                        "size": 12,
                                                        "sort": {
                                                            "empty": false,
                                                            "sorted": true,
                                                            "unsorted": false
                                                        },
                                                        "first": true,
                                                        "last": true,
                                                        "hasNext": false,
                                                        "totalPages": 1,
                                                        "totalElements": 2,
                                                        "numberOfElements": 2,
                                                        "empty": false
                                                    }
                                                },
                                                "message": null
                                            }
                                            """
                            ))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                                examples = {
                                @ExampleObject(name = "비로그인 사용자",
                                                value = "{\"status\" : \"fail\", \"data\" : null, \"message\" : \"내 글 조회 실패: 비로그인 사용자입니다.\"}"
                                        ),
                                @ExampleObject(name="접근 권한 없음",
                                        value = "{\"status\" : \"fail\", \"data\" : null, \"message\" : \"내 글 조회 실패: 접근 권한이 없습니다.\"}"
                                )
                                })),

            })
    <T extends SimplePostDto> ApiResponse<PageCustom<T>> findMyPost(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PageableDefault(size = 12, sort = "createDate", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false, value = "q", defaultValue = "") String q);


    @Operation(summary = "추천 게시글 조회", description = "선호 종목에 맞춘 추천 게시글을 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "유저 게시글 리스트",
                            content=@Content(mediaType = "application/json", schema = @Schema(oneOf = {SimplePostShortDtoResponse.class, SimplePostLongDtoResponse.class}),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "status": "success",
                                                         "data": {
                                                             "content": [
                                                                 {
                                                                     "postId": 2,
                                                                     "meetingStatus": "RECRUIT",
                                                                     "gameType": "BASKETBALL",
                                                                     "location": "SEOUL",
                                                                     "title": "test21",
                                                                     "meetingMemberNum": 3,
                                                                     "meetingDeadline": "2024-12-11",
                                                                     "meetingTime": "15:00:00",
                                                                     "meetingDays": "MON;WED;FRI",
                                                                     "bookmarked": false
                                                                 },
                                                                 {
                                                                     "postId": 1,
                                                                     "meetingStatus": "RECRUIT",
                                                                     "gameType": "BASKETBALL",
                                                                     "location": "SEOUL",
                                                                     "title": "test21",
                                                                     "meetingMemberNum": 3,
                                                                     "meetingDeadline": "2024-12-11",
                                                                     "meetingTime": "15:00:00",
                                                                     "meetingDays": "MON;WED;FRI",
                                                                     "bookmarked": false
                                                                 }
                                                             ],
                                                             "pageable": {
                                                                 "number": 1,
                                                                 "size": 12,
                                                                 "sort": {
                                                                     "empty": false,
                                                                     "sorted": true,
                                                                     "unsorted": false
                                                                 },
                                                                 "first": true,
                                                                 "last": true,
                                                                 "hasNext": false,
                                                                 "totalPages": 1,
                                                                 "totalElements": 2,
                                                                 "numberOfElements": 2,
                                                                 "empty": false
                                                             }
                                                         },
                                                         "message": null
                                                     }
                                                    """
                                    ))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "추천글 조회 권한 없음",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            value = "{\"status\" : \"fail\", \"data\" : null, \"message\" : \"추천글 조회 실패: 권한이 없습니다.\"}"
                                    )))
            })
    <T extends SimplePostDto> ApiResponse<List<T>> findRecommendPost(
            @AuthenticationPrincipal CustomUserDetails principal);
}