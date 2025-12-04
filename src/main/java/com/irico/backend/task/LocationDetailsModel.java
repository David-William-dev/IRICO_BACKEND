package com.irico.backend.task;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class LocationDetailsModel {
    private List<String> locations;
    private List<FlimModel> flim;

    public LocationDetailsModel() {
        this.flim = new ArrayList<>();
        this.locations = new ArrayList<String>();
    }

    public Boolean addNewFlim(FlimModel flim) {
        return this.flim.add(flim);
    }

    public Boolean deleteFlim(FlimModel flim) {
        return this.flim.remove(flim);
    }
}
