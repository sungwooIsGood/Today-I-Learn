![Untitled](https://github.com/user-attachments/assets/cebcdf63-58cb-455d-8426-7366d5d99027)
- 프로젝트를 진행하면서 **API당 별도의 DTO 클래스를 만들어서 중간에 엔티티를 DTO로 변환하고 있었다.**
    - 그러던 중 StackOverflowError가 발생하였다.
- PostDto.java

```java
import com.chimhahaha.domain.Post;
import lombok.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class PostDto {

    private Long id;

    @NotEmpty(message = "제목을 입력해주세요.")
    private String title;

    @NotEmpty(message = "먼저 로그인을 해주세요.")
    private String writer;

    private String content;

    private LocalDate date;

    private int cnt;

    private int like;

    private List<PhotoDto> photos;

    private List<ReplyDto> replys;
    public PostDto(Post post){
        id = post.getId();
        title = post.getTitle();
        writer = post.getWriter();
        content = post.getContent();
        date = post.getDate();
        cnt = post.getCnt();
        like = post.getLike();
        photos = post.getPhotos().stream()
                .map(photo -> new PhotoDto(photo))
                .collect(Collectors.toList());
        replys = post.getReplys().stream()
                .map(reply -> new ReplyDto(reply))
                .collect(Collectors.toList());

    }
}
```

- PhotoDto.java

```java
import com.chimhahaha.domain.Photo;
import com.chimhahaha.domain.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PhotoDto {

    private Long id;

    private Post post;

    private String fileName;

    private String path;

    public PhotoDto(Photo photo){
        id = photo.getId();
        post = photo.getPost();
        fileName = photo.getFileName();
        path = photo.getPath();
    }
}
```

- PhotoDto를 잘 보면 생성자 안에 Post클래스를 그대로 반환하고 있다.
- Dto 생성 후 Dto 내에서 **Post 엔티티를 조회하게 되면 순환참조가 발생하게 된 것이었다. 그렇기 때문에 Post 객체를 그대로 받는 것이 아닌 Post의 키값을 반환 해야했던 것이다.**
    - `post = photo.getPost();`로 인해 꽤 고생했다….
    - `Post.getPost().getId()`로 수정하였다.

- PhotoDto.java(수정)

```java
import com.chimhahaha.domain.Photo;
import com.chimhahaha.domain.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PhotoDto {

    private Long id;

    private Long post;

    private String fileName;

    private String path;

    public PhotoDto(Photo photo){
        id = photo.getId();
        post = photo.getPost().getId();
        fileName = photo.getFileName();
        path = photo.getPath();
    }
}
```

- 정상작동 된다!

![Untitled (1)](https://github.com/user-attachments/assets/59464e9c-1d2e-4447-b406-7fdaf2d175ae)