package atlassian.controller;


import atlassian.Neo4jLoader;
import com.atlassian.connect.spring.AddonInstalledEvent;
import com.atlassian.connect.spring.AtlassianHostRepository;
import com.atlassian.connect.spring.AtlassianHostRestClients;
import handlers.HookHandler;
import org.json.JSONObject;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@org.springframework.stereotype.Controller
public class Controller {
    private final HookHandler handler;
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
    @Autowired
    private AtlassianHostRestClients atlassianHostRestClients;

    public Controller() {
        this.handler = new HookHandler(new Neo4jLoader());
    }

    @EventListener
    public void processAddonInstalledEvent(AddonInstalledEvent event) throws MalformedURLException {
        logger.info(event.toString());
        ScheduledExecutorService executorService = Executors
                .newSingleThreadScheduledExecutor();
        executorService.schedule(() -> {
            String projects = atlassianHostRestClients.authenticatedAsAddon().getForObject("/rest/api/3/project/search", String.class);
            JSONObject proj_info = new JSONObject(projects);
            JSONArray arr1 = proj_info.getJSONArray("projects");
            handler.handleProjUpdated(arr1);
            int i = 0;
            while (true) {
                String issues = atlassianHostRestClients.authenticatedAsAddon(event.getHost()).getForObject(String.format("/rest/api/3/search?maxResults=100&startAt=%d", i), String.class);
                i += 100;
                JSONObject issues_info = new JSONObject(issues);
                JSONArray arr = issues_info.getJSONArray("issues");
                handler.handleIssueUpdated(arr);
                if (arr.length() < 100) {
                    break;
                }
            }

        }, 15, TimeUnit.SECONDS);

    }

    @ResponseBody
    @RequestMapping(consumes="application/json", value = "/issue_updated", method = RequestMethod.POST)
    public void issue_updated(@RequestBody String json){
        logger.info(json);
        JSONObject issue_info = (new JSONObject(json)).getJSONObject("issue");
        handler.handleIssueUpdated(issue_info);
    }

    @ResponseBody
    @RequestMapping(consumes="application/json", value = "/issue_deleted", method = RequestMethod.POST)
    public void issue_deleted(@RequestBody JSONObject json){
        logger.info(json.toString());
        JSONObject issue_info = (new JSONObject(json)).getJSONObject("issue");
        handler.handleIssueDeleted(new JSONObject(json));
    }

    @ResponseBody
    @RequestMapping(consumes="application/json", value = "/project_updated", method = RequestMethod.POST)
    public void project_updated(@RequestBody String json){
        logger.info(json);
        JSONObject proj_info = (new JSONObject(json)).getJSONObject("project");
        handler.handleProjUpdated(proj_info);
    }

    @ResponseBody
    @RequestMapping(consumes="application/json", value = "/project_deleted", method = RequestMethod.POST)
    public void project_deleted(@RequestBody String json){
        logger.info(json);
        handler.handleProjDeleted(json);
    }


    @ResponseBody
    @RequestMapping(consumes="application/json", value = "/user_updated", method = RequestMethod.POST)
    public void user_updated(@RequestBody String json){
        logger.info(json);
        JSONObject user_info = (new JSONObject(json)).getJSONObject("user");
        logger.info(user_info.toString());
        handler.handleProjUpdated(proj_info);
    }

    @ResponseBody
    @RequestMapping(consumes="application/json", value = "/user_deleted", method = RequestMethod.POST)
    public void user_deleted(@RequestBody String json){
        logger.info(json);
        JSONObject user_info = (new JSONObject(json)).getJSONObject("user");
        logger.info(user_info.toString());
        handler.handleProjUpdated(proj_info);
    }
}