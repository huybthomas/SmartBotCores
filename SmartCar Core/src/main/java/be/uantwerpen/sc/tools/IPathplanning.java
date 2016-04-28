package be.uantwerpen.sc.tools;

import be.uantwerpen.sc.models.map.MapJson;

import java.util.List;

/**
 * Created by Niels on 27/04/2016.
 */

public interface IPathplanning {

    List<Vertex> Calculatepath(MapJson mapJson, int start, int stop);

}
