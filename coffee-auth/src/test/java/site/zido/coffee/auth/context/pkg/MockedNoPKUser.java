package site.zido.coffee.auth.context.pkg;

import site.zido.coffee.auth.entity.IUser;

public class MockedNoPKUser implements IUser {
    private static final long serialVersionUID = 7341859992235187927L;
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
