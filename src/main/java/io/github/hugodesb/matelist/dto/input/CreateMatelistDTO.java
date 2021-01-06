package io.github.hugodesb.matelist.dto.input;

public class CreateMatelistDTO {

    private Long user_id;
    private boolean createNew;
    private String platformPlaylistId;

    public CreateMatelistDTO() {
        this.createNew = false;
    }

    public CreateMatelistDTO(Long user_id) {
        this.user_id = user_id;
        this.createNew = false;
    }

    public CreateMatelistDTO(Long user_id, boolean createNew, String platformPlaylistId) {
        this.user_id = user_id;
        this.createNew = createNew;
        this.platformPlaylistId = platformPlaylistId;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public boolean isCreateNew() {
        return createNew;
    }

    public void setCreateNew(boolean createNew) {
        this.createNew = createNew;
    }

    public String getPlatformPlaylistId() {
        return platformPlaylistId;
    }

    public void setPlatformPlaylistId(String platformPlaylistId) {
        this.platformPlaylistId = platformPlaylistId;
    }

    @Override
    public String toString() {
        return "CreateMatelistDTO{" +
                "user_id='" + user_id + '\'' +
                ", createNew=" + createNew +
                ", existingPlaylistId='" + platformPlaylistId + '\'' +
                '}';
    }
}
