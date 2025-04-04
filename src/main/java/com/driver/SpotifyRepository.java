package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
//
    public HashMap<Artist, List<Album>> artistAlbumMap;
//
    public HashMap<Album, List<Song>> albumSongMap;
//
    public HashMap<Playlist, List<Song>> playlistSongMap;
//
    public HashMap<Playlist, List<User>> playlistListenerMap;
//
    public HashMap<User, Playlist> creatorPlaylistMap;
//
    public HashMap<User, List<Playlist>> userPlaylistMap;

    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name,mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Artist artist = null;

        for(int i=0;i<artists.size();i++) {
            if(artists.get(i).getName().equals(artistName)){
                artist = artists.get(i);
                break;
            }
        }

        if(artist == null){
            artist = new Artist(artistName);
            artists.add(artist);
        }
        Album album = new Album(title);
        albums.add(album);

        List<Album>  list = artistAlbumMap.getOrDefault(artist,new ArrayList<>());
        list.add(album);
        artistAlbumMap.put(artist,list);

        return  album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{

        Album album = null;
        for(int i=0;i<albums.size();i++){
            if(albums.get(i).getTitle().equals(albumName)){
                album = albums.get(i);
                break;
            }
        }
        if(album == null){
            throw new Exception();
        }

        Song song = new Song(title,length);
        songs.add(song);

        List<Song> list = albumSongMap.getOrDefault(album,new ArrayList<>());
        list.add(song);
        albumSongMap.put(album,list);

        return song;

    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User user = null;
        for(int i=0;i<users.size();i++){
            if(users.get(i).getMobile().equals(mobile)){
                user = users.get(i);
                break;
            }
        }
        if(user == null){
            throw new Exception();
        }
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        List<Song> songList = new ArrayList<>();
        for(int i=0;i<songs.size();i++){
            if(songs.get(i).getLength() == length){
                songList.add(songs.get(i));
            }
        }

        playlistSongMap.put(playlist,songList);

        List<User> userList = playlistListenerMap.getOrDefault(playlist,new ArrayList<>());
        userList.add(user);
        playlistListenerMap.put(playlist,userList);

        creatorPlaylistMap.put(user,playlist);

        List<Playlist> playlistList =  userPlaylistMap.getOrDefault(user,new ArrayList<>());
        playlistList.add(playlist);
        userPlaylistMap.put(user,playlistList);



        return playlist;

    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User user = null;
        for(int i=0;i<users.size();i++){
            if(users.get(i).getMobile().equals(mobile)){
                user = users.get(i);
                break;
            }
        }
        if(user == null){
            throw new Exception();
        }
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);

        List<Song> songList = new ArrayList<>();
        for(int i=0;i<songTitles.size();i++){
            for(int j=0;j<songs.size();j++){
                if(songs.get(j).getTitle().equals(songTitles.get(i))){
                    songList.add(songs.get(j));
                    break;
                }
            }
        }

        playlistSongMap.put(playlist,songList);

        List<User> userList = playlistListenerMap.getOrDefault(playlist,new ArrayList<>());
        userList.add(user);
        playlistListenerMap.put(playlist,userList);

        creatorPlaylistMap.put(user,playlist);

        List<Playlist> playlistList =  userPlaylistMap.getOrDefault(user,new ArrayList<>());
        playlistList.add(playlist);
        userPlaylistMap.put(user,playlistList);



        return playlist;


    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User user = null;
        for(int i=0;i<users.size();i++){
            if(users.get(i).getMobile().equals(mobile)){
                user = users.get(i);
                break;
            }
        }

        if(user == null){
            throw new Exception("User does not exist");
        }

        Playlist playlist = null;
        for(int i=0;i<playlists.size();i++){
            if(playlists.get(i).getTitle().equals(playlistTitle)){
                playlist = playlists.get(i);
                break;
            }
        }

        if(playlist == null){
            throw new Exception("Playlist does not exist");
        }
        if(creatorPlaylistMap.containsKey(user) && creatorPlaylistMap.get(user).equals(playlist)){
            return playlist;
        }
        List<User> userList = playlistListenerMap.getOrDefault(playlist,new ArrayList<>());
        userList.add(user);
        playlistListenerMap.put(playlist,userList);

        List<Playlist> playlistList = userPlaylistMap.getOrDefault(user,new ArrayList<>());
        playlistList.add(playlist);
        userPlaylistMap.put(user,playlistList);

        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        // 1. Find user by mobile
        User user = null;
        for (User u : users) {
            if (u.getMobile().equals(mobile)) {
                user = u;
                break;
            }
        }
        if (user == null) {
            throw new Exception("User does not exist");
        }

        // 2. Find song by title
        Song song = null;
        for (Song s : songs) {
            if (s.getTitle().equals(songTitle)) {
                song = s;
                break;
            }
        }
        if (song == null) {
            throw new Exception("Song does not exist");
        }

        // 3. Check if song already liked by user
        List<User> likedUsers = songLikeMap.getOrDefault(song, new ArrayList<>());

        if (!likedUsers.contains(user)) {
            likedUsers.add(user);
            songLikeMap.put(song, likedUsers);

            // 4. Increase song like count
            song.setLikes(song.getLikes() + 1);

            // 5. Find album for this song
            Album songAlbum = null;
            for (Map.Entry<Album, List<Song>> entry : albumSongMap.entrySet()) {
                if (entry.getValue().contains(song)) {
                    songAlbum = entry.getKey();
                    break;
                }
            }

            // 6. Find artist for the album
            Artist songArtist = null;
            for (Map.Entry<Artist, List<Album>> entry : artistAlbumMap.entrySet()) {
                if (entry.getValue().contains(songAlbum)) {
                    songArtist = entry.getKey();
                    break;
                }
            }

            // 7. Increase artist like count
            if (songArtist != null) {
                songArtist.setLikes(songArtist.getLikes() + 1);
            }
        }

        return song;
    }

    public String mostPopularArtist() {
        int maxLike = -1;
        Artist maxArtist = null;

        for(Artist a : artists){
            if(a.getLikes()  > maxLike){
                maxArtist = a;
                maxLike = a.getLikes();
            }
        }
        if(maxArtist == null){
            return "";
        }
        return maxArtist.getName();


    }

    public String mostPopularSong() {
        int maxLike = -1;
        Song maxSong = null;

        for(Song s : songs){
            if(s.getLikes()  > maxLike){
                maxSong = s;
                maxLike = s.getLikes();
            }
        }
        if(maxSong == null){
            return "";
        }
        return maxSong.getTitle();
    }
}
