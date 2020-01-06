package com.guider.health.arouter_core.template;




import com.guider.health.arouter_annotation.model.RouteMeta;

import java.util.Map;

/**
 * @author Lance
 * @date 2018/2/22
 */

public interface IRouteGroup {

    void loadInto(Map<String, RouteMeta> atlas);
}
