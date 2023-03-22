package jpabook.jpashop.api;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        return new CreateMemberResponse(memberService.join(member));
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long joined = memberService.join(member);
        return new CreateMemberResponse(joined);
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }
    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @PatchMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request) {

        memberService.update(id, request.getName());
        Member updatedMember = memberService.findOne(id);
        return new UpdateMemberResponse(updatedMember.getId(), updatedMember.getName());
    }
    @Data
    static class UpdateMemberRequest {
        @NotEmpty
        private String name;
    }
    @Data
    static class UpdateMemberResponse {
        private Long id;
        private String name;

        public UpdateMemberResponse(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @GetMapping("/api/v1/members")
    public List<Member> memberListV1() {
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result<ListMemberResponse> memberListV2() {
        List<ListMemberResponse> collect = memberService.findMembers().stream()
                .map(m -> new ListMemberResponse(m.getId(), m.getName()))
                .collect(Collectors.toList());
        return new Result(collect);
    }

    @Data
    static class Result<T> {
        private T data;

        public Result(T data) {
            this.data = data;
        }
    }

    @Data
    static class ListMemberResponse {
        private Long id;
        private String name;

        public ListMemberResponse(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
