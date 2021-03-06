package com.supermap.imobilelite.trafficsamples.util;

import java.util.ArrayList;
import java.util.List;

import com.supermap.imobilelite.commons.EventStatus;
import com.supermap.imobilelite.maps.Point2D;
import com.supermap.imobilelite.networkAnalyst.ClosestFacilityPath;
import com.supermap.imobilelite.networkAnalyst.DemandResult;
import com.supermap.imobilelite.networkAnalyst.FindClosestFacilitiesParameters;
import com.supermap.imobilelite.networkAnalyst.FindClosestFacilitiesResult;
import com.supermap.imobilelite.networkAnalyst.FindClosestFacilitiesService;
import com.supermap.imobilelite.networkAnalyst.FindClosestFacilitiesService.FindClosestFacilitiesEventListener;
import com.supermap.imobilelite.networkAnalyst.FindLocationParameters;
import com.supermap.imobilelite.networkAnalyst.FindLocationResult;
import com.supermap.imobilelite.networkAnalyst.FindLocationService;
import com.supermap.imobilelite.networkAnalyst.FindLocationService.FindLocationEventListener;
import com.supermap.imobilelite.networkAnalyst.FindMTSPPathsParameters;
import com.supermap.imobilelite.networkAnalyst.FindMTSPPathsResult;
import com.supermap.imobilelite.networkAnalyst.FindMTSPPathsService;
import com.supermap.imobilelite.networkAnalyst.FindMTSPPathsService.FindMTSPPathsEventListener;
import com.supermap.imobilelite.networkAnalyst.FindPathParameters;
import com.supermap.imobilelite.networkAnalyst.FindPathResult;
import com.supermap.imobilelite.networkAnalyst.FindPathService;
import com.supermap.imobilelite.networkAnalyst.FindPathService.FindPathEventListener;
import com.supermap.imobilelite.networkAnalyst.FindServiceAreasParameters;
import com.supermap.imobilelite.networkAnalyst.FindServiceAreasResult;
import com.supermap.imobilelite.networkAnalyst.FindServiceAreasService;
import com.supermap.imobilelite.networkAnalyst.FindServiceAreasService.FindServiceAreasEventListener;
import com.supermap.imobilelite.networkAnalyst.FindTSPPathsParameters;
import com.supermap.imobilelite.networkAnalyst.FindTSPPathsResult;
import com.supermap.imobilelite.networkAnalyst.FindTSPPathsService;
import com.supermap.imobilelite.networkAnalyst.FindTSPPathsService.FindTSPPathsEventListener;
import com.supermap.imobilelite.networkAnalyst.ServiceAreaResult;
import com.supermap.imobilelite.networkAnalyst.SupplyCenter;
import com.supermap.imobilelite.networkAnalyst.TransportationAnalystParameter;
import com.supermap.imobilelite.networkAnalyst.TransportationAnalystResultSetting;
import com.supermap.services.components.commontypes.Geometry;
import com.supermap.services.components.commontypes.MTSPPath;
import com.supermap.services.components.commontypes.Path;
import com.supermap.services.components.commontypes.Route;
import com.supermap.services.components.commontypes.TSPPath;

/**
 * <p>
 * ?????????????????????????????????????????????????????????????????????????????????????????????????????????
 * </p>
 * @author ${Author}
 * @version ${Version}
 * 
 */
public class NetWorkAnalystUtil {

    /**
     * <p>
     * ????????????????????????
     * </p>
     * @param url ????????????????????????
     * @param geoPoints ???????????????????????????
     * @return ???????????????Points??????
     */
    public static List<List<Point2D>> excutePathService(String url, List<Point2D> geoPoints) {
        if (url == null || "".equals(url) || geoPoints == null) {
            return null;
        }
        // ??????????????????????????????
        FindPathParameters params = new FindPathParameters();
        Point2D[] nodes = new Point2D[geoPoints.size()];
        for (int i = 0; i < geoPoints.size(); i++) {
            nodes[i] = geoPoints.get(i);
        }
        params.nodes = nodes;
        params.parameter = GetGeneralParam();

        // ????????????????????????
        FindPathService path = new FindPathService(url);
        MyFindPathEventListener listener = new MyFindPathEventListener();
        path.process(params, listener);
        try {
            listener.waitUntilProcessed();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ???????????????????????????????????????Route????????????Points?????????????????????????????????????????????
        FindPathResult pathResult = listener.getReult();
        if (pathResult != null && pathResult.pathList != null) {
            Path[] pathList = pathResult.pathList;
            List<List<Point2D>> pointsList = new ArrayList<List<Point2D>>();
            for (int i = 0; i < pathList.length; i++) {
                List<Point2D> points = new ArrayList<Point2D>();
                Route route = pathList[i].route;
                if (route != null && route.points != null) {
                    for (int k = 0; k < route.points.length; k++) {
                        points.add(new Point2D(route.points[k].x, route.points[k].y));
                    }
                }
                pointsList.add(points);
            }
            return pointsList;
        }
        return null;
    }

    /**
     * <p>
     * ?????????????????????
     * </p>
     * @param url ????????????????????????
     * @param geoPoints ???????????????????????????
     * @return ???????????????Points??????
     */
    public static List<List<Point2D>> excuteTSPPathsService(String url, List<Point2D> geoPoints) {
        if (url == null || "".equals(url) || geoPoints == null) {
            return null;
        }
        // ???????????????????????????
        FindTSPPathsParameters params = new FindTSPPathsParameters();
        Point2D[] nodes = new Point2D[geoPoints.size()];
        for (int i = 0; i < geoPoints.size(); i++) {
            nodes[i] = geoPoints.get(i);
        }
        params.nodes = nodes;
        params.endNodeAssigned = true;
        params.parameter = GetGeneralParam();

        // ?????????????????????
        FindTSPPathsService tsp = new FindTSPPathsService(url);
        MyFindTSPPathsEventListener listener = new MyFindTSPPathsEventListener();
        tsp.process(params, listener);
        try {
            listener.waitUntilProcessed();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ????????????????????????????????????Route????????????Points?????????????????????????????????????????????
        FindTSPPathsResult tspResult = listener.getResult();
        if (tspResult != null && tspResult.tspPathList != null) {
            TSPPath[] tspPathList = tspResult.tspPathList;
            List<List<Point2D>> pointsList = new ArrayList<List<Point2D>>();
            for (int i = 0; i < tspPathList.length; i++) {
                List<Point2D> points = new ArrayList<Point2D>();
                TSPPath tspPath = tspPathList[i];
                if (tspPath != null && tspPath.route != null && tspPath.route.points != null) {
                    Route route = tspPath.route;
                    for (int k = 0; k < route.points.length; k++) {
                        points.add(new Point2D(route.points[k].x, route.points[k].y));
                    }
                    pointsList.add(points);
                }
            }
            return pointsList;
        }

        return null;
    }

    /**
     * <p>
     * ????????????????????????
     * </p>
     * @param url ????????????????????????
     * @param geoNodes ???????????????????????????
     * @param geoCenters ????????????????????????
     * @return ???????????????Points??????
     */
    public static List<List<Point2D>> excuteMTSPPathService(String url, List<Point2D> geoNodes, List<Point2D> geoCenters) {
        if (url == null || "".equals(url) || geoNodes == null || geoCenters == null) {
            return null;
        }
        // ??????????????????????????????
        FindMTSPPathsParameters params = new FindMTSPPathsParameters();
        Point2D[] nodes = new Point2D[geoNodes.size()];
        for (int i = 0; i < geoNodes.size(); i++) {
            nodes[i] = geoNodes.get(i);
        }
        Point2D[] centers = new Point2D[geoCenters.size()];
        for (int i = 0; i < geoCenters.size(); i++) {
            centers[i] = geoCenters.get(i);
        }
        params.nodes = nodes;
        params.centers = centers;
        params.hasLeastTotalCost = false;
        params.parameter = GetGeneralParam();

        // ????????????????????????
        FindMTSPPathsService mtsp = new FindMTSPPathsService(url);
        MyFindMTSPPathsEventListener listener = new MyFindMTSPPathsEventListener();
        mtsp.process(params, listener);
        try {
            listener.waitUntilProcessed();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ???????????????????????????????????????Route????????????Points?????????????????????????????????????????????
        FindMTSPPathsResult mtspResult = listener.getResult();
        if (mtspResult != null && mtspResult.pathList != null) {
            MTSPPath[] mtspPathList = mtspResult.pathList;
            List<List<Point2D>> PointsList = new ArrayList<List<Point2D>>();
            for (int i = 0; i < mtspPathList.length; i++) {
                List<Point2D> points = new ArrayList<Point2D>();
                Route route = mtspPathList[i].route;
                if (route != null && route.points != null) {
                    for (int k = 0; k < route.points.length; k++) {
                        points.add(new Point2D(route.points[k].x, route.points[k].y));
                    }
                    PointsList.add(points);
                }
            }
            return PointsList;
        }

        return null;
    }

    /**
     * <p>
     * ????????????????????????
     * </p>
     * @param url ????????????????????????
     * @param geoFacilities ???????????????
     * @param event ?????????
     * @return ???????????????Points??????
     */
    public static List<List<Point2D>> excuteFindClosesFacilitiesService(String url, List<Point2D> geoFacilities, Point2D event) {
        if (url == null || "".equals(url) || geoFacilities == null || event == null) {
            return null;
        }
        // ????????????????????????????????????
        FindClosestFacilitiesParameters params = new FindClosestFacilitiesParameters();
        Point2D[] facilities = new Point2D[geoFacilities.size()];
        for (int i = 0; i < geoFacilities.size(); i++) {
            facilities[i] = geoFacilities.get(i);
        }
        params.facilities = facilities;
        params.event = event;
        params.expectFacilityCount = 1;
        params.parameter = GetGeneralParam();

        // ????????????????????????
        FindClosestFacilitiesService cft = new FindClosestFacilitiesService(url);
        MyFindClosestFacilitiesEventListener listener = new MyFindClosestFacilitiesEventListener();
        cft.process(params, listener);
        try {
            listener.waitUntilProcessed();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ?????????????????????????????????????????????Route????????????Points?????????????????????????????????????????????
        FindClosestFacilitiesResult closestResult = listener.getResult();
        if (closestResult != null && closestResult.facilityPathList != null) {
            ClosestFacilityPath[] closestList = closestResult.facilityPathList;
            List<List<Point2D>> pointsList = new ArrayList<List<Point2D>>();
            for (int i = 0; i < closestList.length; i++) {
                List<Point2D> points = new ArrayList<Point2D>();
                Route route = closestList[i].route;
                if (route != null && route.points != null) {
                    for (int k = 0; k < route.points.length; k++) {
                        points.add(new Point2D(route.points[k].x, route.points[k].y));
                    }
                    pointsList.add(points);
                }
            }
            return pointsList;
        }
        return null;
    }

    /**
     * <p>
     * ?????????????????????
     * </p>
     * @param url ????????????????????????
     * @param geoCenters ????????????????????????,????????????
     * @return
     */
    public static List<List<Point2D>> excuteServiceAreasService(String url, List<Point2D> geoCenters) {
        if (url == null || "".equals(url) || geoCenters == null) {
            return null;
        }
        // ???????????????????????????
        FindServiceAreasParameters params = new FindServiceAreasParameters();
        double[] weights = new double[geoCenters.size()];
        Point2D[] centers = new Point2D[geoCenters.size()];
        for (int i = 0; i < geoCenters.size(); i++) {
            centers[i] = geoCenters.get(i);
            weights[i] = 500;
        }
        params.centers = centers;// new Integer[]{2,4};
        params.isFromCenter = true;
        params.isCenterMutuallyExclusive = true;
        params.weights = weights;// new double[]{500,1000};
        TransportationAnalystParameter parameter = new TransportationAnalystParameter();
        parameter.weightFieldName = "length";
        parameter.turnWeightField = "TurnCost";
        params.parameter = parameter;

        // ?????????????????????
        FindServiceAreasService sas = new FindServiceAreasService(url);
        MyFindServiceAreasEventListener listener = new MyFindServiceAreasEventListener();
        sas.process(params, listener);
        try {
            listener.waitUntilProcessed();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ????????????????????????????????????serviceRegion????????????Points?????????????????????????????????????????????
        FindServiceAreasResult serviceAreaResult = listener.getResult();
        if (serviceAreaResult != null && serviceAreaResult.serviceAreaList != null) {
            ServiceAreaResult[] serviceAreaList = serviceAreaResult.serviceAreaList;
            List<List<Point2D>> pointsList = new ArrayList<List<Point2D>>();
            for (int i = 0; i < serviceAreaList.length; i++) {
                List<Point2D> points = new ArrayList<Point2D>();
                ServiceAreaResult result = serviceAreaList[i];
                if (result != null && result.serviceRegion != null && result.serviceRegion.points != null) {
                    Geometry serviceRegion = result.serviceRegion;
                    for (int k = 0; k < serviceRegion.points.length; k++) {
                        points.add(new Point2D(serviceRegion.points[k].x, serviceRegion.points[k].y));
                    }
                    pointsList.add(points);
                }
            }
            return pointsList;
        }

        return null;
    }

    /**
     * <p>
     * ????????????????????????
     * </p>
     * @param url ????????????????????????
     * @param supplyCenters ???????????????????????????,????????????
     * @return
     */
    public static List<List<Point2D>> excuteFindLocationService(String url, SupplyCenter[] supplyCenters) {
        if (url == null || "".equals(url) || supplyCenters == null) {
            return null;
        }
        // ??????????????????????????????
        FindLocationParameters params = new FindLocationParameters();
        params.expectedSupplyCenterCount = 2;
        params.supplyCenters = supplyCenters;
        params.turnWeightField = "TurnCost";
        params.weightName = "length";
        params.isFromCenter = false;
        // ????????????????????????
        FindLocationService loc = new FindLocationService(url);
        MyFindLocationEventListener listener = new MyFindLocationEventListener();
        loc.process(params, listener);
        try {
            listener.waitUntilProcessed();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ???????????????????????????????????????geometry????????????Points?????????????????????????????????????????????
        FindLocationResult locationResult = listener.getResult();
        if (locationResult != null && locationResult.demandResults != null) {
            DemandResult[] demandResultList = locationResult.demandResults;
            List<List<Point2D>> pointsList = new ArrayList<List<Point2D>>();
            for (int i = 0; i < demandResultList.length; i++) {
                List<Point2D> points = new ArrayList<Point2D>();
                DemandResult result = demandResultList[i];
                if (result != null && result.geometry != null && result.geometry.points != null) {
                    Geometry geometry = result.geometry;
                    for (int k = 0; k < geometry.points.length; k++) {
                        points.add(new Point2D(geometry.points[k].x, geometry.points[k].y));
                    }
                    pointsList.add(points);
                }
            }
            return pointsList;
        }
        return null;
    }

    /**
     * <p>
     * ??????????????????????????????
     * </p>
     * @return
     */
    public static TransportationAnalystParameter GetGeneralParam() {

        // ????????????????????????????????????????????????????????????????????????????????????
        TransportationAnalystResultSetting resultSetting = new TransportationAnalystResultSetting();
        resultSetting.returnEdgeFeatures = false;
        resultSetting.returnEdgeGeometry = false;
        resultSetting.returnRoutes = true;

        // ????????????????????????????????????
        TransportationAnalystParameter parameter = new TransportationAnalystParameter();
        parameter.weightFieldName = "length";
        parameter.turnWeightField = "TurnCost";
        parameter.resultSetting = resultSetting;
        return parameter;

    }

    /**
     * <p>
     * ?????????????????????????????????????????????????????????????????????????????????
     * </p>
     * @author ${Author}
     * @version ${Version}
     * 
     */
    static class MyFindPathEventListener extends FindPathEventListener {
        private FindPathResult pathResult;

        public MyFindPathEventListener() {
            super();
            // TODO Auto-generated constructor stub
        }

        public FindPathResult getReult() {
            return pathResult;
        }

        @Override
        public void onFindPathStatusChanged(Object sourceObject, EventStatus status) {
            if (sourceObject instanceof FindPathResult) {
                pathResult = (FindPathResult) sourceObject;
            }
        }

    }

    /**
     * <p>
     * ??????????????????????????????????????????????????????????????????????????????
     * </p>
     * @author ${Author}
     * @version ${Version}
     * 
     */
    static class MyFindTSPPathsEventListener extends FindTSPPathsEventListener {
        private FindTSPPathsResult tspPathsResult;

        public MyFindTSPPathsEventListener() {
            super();
        }

        public FindTSPPathsResult getResult() {
            return tspPathsResult;
        }

        @Override
        public void onFindTSPPathsStatusChanged(Object sourceObject, EventStatus status) {
            if (sourceObject instanceof FindTSPPathsResult) {
                tspPathsResult = (FindTSPPathsResult) sourceObject;
            }
        }

    }

    /**
     * <p>
     * ?????????????????????????????????????????????????????????????????????????????????
     * </p>
     * @author ${Author}
     * @version ${Version}
     * 
     */
    static class MyFindMTSPPathsEventListener extends FindMTSPPathsEventListener {
        private FindMTSPPathsResult mtspPathResult;

        public MyFindMTSPPathsEventListener() {
            super();
            // TODO Auto-generated constructor stub
        }

        public FindMTSPPathsResult getResult() {
            return mtspPathResult;
        }

        @Override
        public void onFindMTSPPathsStatusChanged(Object sourceObject, EventStatus status) {
            if (sourceObject instanceof FindMTSPPathsResult) {
                mtspPathResult = (FindMTSPPathsResult) sourceObject;
            }
        }

    }

    /**
     * <p>
     * ???????????????????????????????????????????????????????????????????????????
     * </p>
     * @author ${Author}
     * @version ${Version}
     * 
     */
    static class MyFindClosestFacilitiesEventListener extends FindClosestFacilitiesEventListener {
        private FindClosestFacilitiesResult closestResult;

        public MyFindClosestFacilitiesEventListener() {
            super();
            // TODO Auto-generated constructor stub
        }

        public FindClosestFacilitiesResult getResult() {
            return closestResult;
        }

        @Override
        public void onFindClosestFacilitiesStatusChanged(Object sourceObject, EventStatus status) {
            if (sourceObject instanceof FindClosestFacilitiesResult) {
                closestResult = (FindClosestFacilitiesResult) sourceObject;
            }
        }

    }

    /**
     * <p>
     * ???????????????????????????????????????????????????????????????????????????
     * </p>
     * @author ${Author}
     * @version ${Version}
     * 
     */
    static class MyFindLocationEventListener extends FindLocationEventListener {
        private FindLocationResult LocationResult;

        public MyFindLocationEventListener() {
            super();
            // TODO Auto-generated constructor stub
        }

        public FindLocationResult getResult() {
            return LocationResult;
        }

        @Override
        public void onFindLocationStatusChanged(Object sourceObject, EventStatus status) {
            if (sourceObject instanceof FindLocationResult) {
                LocationResult = (FindLocationResult) sourceObject;
            }
        }

    }

    /**
     * <p>
     * ??????????????????????????????????????????????????????????????????????????????
     * </p>
     * @author ${Author}
     * @version ${Version}
     * 
     */
    static class MyFindServiceAreasEventListener extends FindServiceAreasEventListener {
        private FindServiceAreasResult serviceAreaResult;

        public MyFindServiceAreasEventListener() {
            super();
        }

        public FindServiceAreasResult getResult() {
            return serviceAreaResult;
        }

        @Override
        public void onFindServiceAreasStatusChanged(Object sourceObject, EventStatus status) {
            if (sourceObject instanceof FindServiceAreasResult) {
                serviceAreaResult = (FindServiceAreasResult) sourceObject;
            }
        }

    }

}
