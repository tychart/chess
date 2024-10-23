// DEPRICATED

package chess;

public interface Records {
    record UserData(String username, String password, String email) {
    }

    record GameData(int gameID, String password, String email) {
    }

    record AuthData(String username, String password, String email) {
    }

}
