package com.example.core.action;

import com.example.core.action.inf.Action;
import com.example.core.service.MenusService;
import org.apache.commons.lang3.StringUtils;

/**
 * @author chenwh3
 */
public class MenusAction extends Action {

    private MenusService menusService;

    private String prefix;
    private String targetName;
    private String menusChPath;

    @Override
    protected void run() {
        if (StringUtils.isAnyBlank(menusChPath, targetName, prefix)) {
            return;
        }
        menusService.buildPermission(prefix, targetName, menusChPath);
    }
}
