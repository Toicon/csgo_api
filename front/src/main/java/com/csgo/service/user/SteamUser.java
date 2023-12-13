package com.csgo.service.user;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author admin
 */
@Getter
@Setter
public class SteamUser {

    private List<SteamPlayer> players;
}
