package dh.example.oauth.entity;

import dh.example.oauth.oauth2.OAuth2UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Entity
@Table(name = "user")
public class User extends BaseDateEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name ="name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "oauth2id")
    private String oauth2Id;

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @Enumerated(EnumType.STRING)
    private Role role;


    public User update(OAuth2UserInfo userInfo) {
        this.name = userInfo.getName();
        this.oauth2Id = userInfo.getOAuth2Id();

        return this;
    }

}
