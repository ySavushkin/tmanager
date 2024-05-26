package mainPackage.tmanager.requests;

import mainPackage.tmanager.enums.UserRoleInProjectE;
import mainPackage.tmanager.models.Project;
import mainPackage.tmanager.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

public class InviteUsersRequest {
    private User requester;
    private List<User> users;
    private Project project;

    private UserRoleInProjectE userRoleInProjectE;

    public InviteUsersRequest(User requester, List<User> users, Project project, UserRoleInProjectE userRoleInProjectE) {
        this.requester = requester;
        this.users = users;
        this.project = project;
        this.userRoleInProjectE = userRoleInProjectE;
    }

    public InviteUsersRequest(User requester, List<User> users, Project project) {
        this.requester = requester;
        this.users = users;
        this.project = project;
    }

    public InviteUsersRequest() {
    }

    public User getRequester() {
        return requester;
    }

    public void setRequester(User requester) {
        this.requester = requester;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public UserRoleInProjectE getUserRoleInProjectE() {
        return userRoleInProjectE;
    }

    public void setUserRoleInProjectE(UserRoleInProjectE userRoleInProjectE) {
        this.userRoleInProjectE = userRoleInProjectE;
    }
}

