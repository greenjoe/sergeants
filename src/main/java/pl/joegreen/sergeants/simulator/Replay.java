package pl.joegreen.sergeants.simulator;

class Replay {
    private int version;
    private int mapWidth;
    private int mapHeight;
    private int[] cities;
    private int[] cityArmies;
    private int[] generals;
    private int[] mountains;
    private String[] usernames;

    public String[] getUsernames() {
        return usernames;
    }

    public void setUsernames(String[] usernames) {
        this.usernames = usernames;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
    }

    public int[] getCities() {
        return cities;
    }

    public void setCities(int[] cities) {
        this.cities = cities;
    }

    public int[] getCityArmies() {
        return cityArmies;
    }

    public void setCityArmies(int[] cityArmies) {
        this.cityArmies = cityArmies;
    }

    public int[] getGenerals() {
        return generals;
    }

    public void setGenerals(int[] generals) {
        this.generals = generals;
    }

    public int[] getMountains() {
        return mountains;
    }

    public void setMountains(int[] mountains) {
        this.mountains = mountains;
    }

    public int getMapWidth() {

        return mapWidth;
    }

    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }
}
