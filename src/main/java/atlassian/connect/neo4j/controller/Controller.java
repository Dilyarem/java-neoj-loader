package atlassian.connect.neo4j.controller;

import atlassian.connect.neo4j.handlers.HookHandler;
import com.atlassian.connect.spring.AddonInstalledEvent;
import com.atlassian.connect.spring.AtlassianHostRestClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.MalformedURLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@org.springframework.stereotype.Controller
public class Controller {
    private final HookHandler handler;
    private final Logger logger;
    @Autowired
    private AtlassianHostRestClients atlassianHostRestClients;

    public Controller(HookHandler hookHandler) {
        this.logger = LoggerFactory.getLogger(Controller.class);
        this.handler = hookHandler;
    }

    @EventListener
    public void processAddonInstalledEvent(AddonInstalledEvent event) throws MalformedURLException {
        logger.info(event.toString());
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> {
            String projects = atlassianHostRestClients.authenticatedAsAddon(event.getHost()).getForObject("/rest/api/3/project/search", String.class);
            logger.info(projects);
            JSONObject projects_info = new JSONObject(projects);
            handler.handleProjUpdated(projects_info.getJSONArray("values"));

            String users = atlassianHostRestClients.authenticatedAsAddon(event.getHost()).getForObject("/rest/api/3/user/search?query=", String.class);
            logger.info(users);
            handler.handleUserUpdated(new JSONArray(users));

            int i = 0;
            while (true) {
                String issues = atlassianHostRestClients.authenticatedAsAddon(event.getHost()).getForObject(String.format("/rest/api/3/search?maxResults=100&startAt=%d", i), String.class);
                i += 100;
                JSONObject issues_info = new JSONObject(issues);
                logger.info(issues);
                handler.handleIssueUpdated(issues_info.getJSONArray("issues"));
                if (issues_info.getJSONArray("issues").length() < 100) {
                    break;
                }
            }
        }, 15, TimeUnit.SECONDS);

    }

    @ResponseBody
    @RequestMapping(consumes="application/json", value = "/issue_created", method = RequestMethod.POST)
    public void issue_created(@RequestBody String web_hook){
        logger.info(web_hook);
        JSONObject issueInfo = (new JSONObject(web_hook)).getJSONObject("issue");
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(issueInfo);
        handler.handleIssueUpdated(jsonArray);
    }

    @ResponseBody
    @RequestMapping(consumes="application/json", value = "/issue_updated", method = RequestMethod.POST)
    public void issue_updated(@RequestBody String web_hook){
        logger.info(web_hook);
        JSONObject issueInfo = (new JSONObject(web_hook)).getJSONObject("issue");
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(issueInfo);
        handler.handleIssueUpdated(jsonArray);
    }

    @ResponseBody
    @RequestMapping(consumes="application/json", value = "/issue_deleted", method = RequestMethod.POST)
    public void issue_deleted(@RequestBody JSONObject json){
        logger.info(json.toString());
        JSONObject issue_info = (new JSONObject(json)).getJSONObject("issue");
        handler.handleIssueDeleted(issue_info);
    }

    @ResponseBody
    @RequestMapping(consumes="application/json", value = "/project_created", method = RequestMethod.POST)
    public void project_created(@RequestBody String json){
        logger.info(json);
        JSONObject proj_info = (new JSONObject(json)).getJSONObject("project");
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(proj_info);
        handler.handleProjUpdated(jsonArray);
    }

    @ResponseBody
    @RequestMapping(consumes="application/json", value = "/project_updated", method = RequestMethod.POST)
    public void project_updated(@RequestBody String json){
        logger.info(json);
        JSONObject proj_info = (new JSONObject(json)).getJSONObject("project");
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(proj_info);
        handler.handleProjUpdated(jsonArray);
    }

    @ResponseBody
    @RequestMapping(consumes="application/json", value = "/project_deleted", method = RequestMethod.POST)
    public void project_deleted(@RequestBody String json){
//        logger.info(json);
//        handler.handleProjDeleted(json);
    }

    @ResponseBody
    @RequestMapping(consumes="application/json", value = "/user_created", method = RequestMethod.POST)
    public void user_created(@RequestBody String json){
        logger.info(json);
        JSONObject userInfo = (new JSONObject(json)).getJSONObject("user");
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(userInfo);
        handler.handleProjUpdated(jsonArray);
    }

    @ResponseBody
    @RequestMapping(consumes="application/json", value = "/user_updated", method = RequestMethod.POST)
    public void user_updated(@RequestBody String json){
        logger.info(json);
        JSONObject userInfo = (new JSONObject(json)).getJSONObject("user");
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(userInfo);
        handler.handleProjUpdated(jsonArray);
    }

    @ResponseBody
    @RequestMapping(consumes="application/json", value = "/user_deleted", method = RequestMethod.POST)
    public void user_deleted(@RequestBody String json){
        logger.info(json);
//        JSONObject userInfo = (new JSONObject(json)).getJSONObject("user");
//        handler.handleUserUpdated(userInfo);
    }
}