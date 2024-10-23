package model;

import java.util.Set;

public record GameListResponse(Set<GameDataSimple> games) {
}
