package com.rationale.emotions.servlets.hub;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openqa.grid.internal.GridRegistry;
import org.openqa.grid.internal.ProxySet;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.web.servlet.RegistryBasedServlet;
import org.openqa.selenium.json.Json;
import org.openqa.selenium.json.JsonOutput;

public class ListNodes extends RegistryBasedServlet {

  private final Json json = new Json();

  public ListNodes() {
    this(null);
  }

  public ListNodes(GridRegistry registry) {
    super(registry);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    process(resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    process(resp);
  }

  private void process(HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.setStatus(200);

    Map<String, Object> proxies = new TreeMap<>();
    try (Writer writer = response.getWriter();
        JsonOutput out = json.newOutput(writer)) {
      proxies.put("success", true);
      proxies.put("nodes", extractAllProxyInfo());
      out.write(proxies);
    }
  }

  private List<Map<String, Object>> extractAllProxyInfo() {
    List<Map<String, Object>> results = new LinkedList<>();
    ProxySet proxies = getRegistry().getAllProxies();
    for (RemoteProxy proxy : proxies) {
      Map<String, Object> res = new TreeMap<>();
      URL url = proxy.getRemoteHost();
      res.put("ip", url.getHost());
      res.put("port", url.getPort());
      Map<String, Object> sessions = new TreeMap<>();
      int total = proxy.getTestSlots().size();
      int used = proxy.getTotalUsed();
      sessions.put("total", total);
      sessions.put("busy", used);
      sessions.put("free", (total - used));
      res.put("sessions", sessions);
      results.add(res);
    }
    return results;
  }

}
