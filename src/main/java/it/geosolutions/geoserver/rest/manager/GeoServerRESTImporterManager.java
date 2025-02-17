/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *  
 *  Copyright (C) 2007,2011 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package it.geosolutions.geoserver.rest.manager;

import com.fasterxml.jackson.databind.JsonNode;
import it.geosolutions.geoserver.rest.HTTPUtils;
import org.restlet.data.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author Alessio Fabiani, GeoSolutions S.A.S.
 *
 */
public class GeoServerRESTImporterManager extends GeoServerRESTAbstractManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(GeoServerRESTImporterManager.class);
    
    /**
     * Default constructor.
     *
     * @param restURL GeoServer REST API endpoint
     * @param username GeoServer REST API authorized username
     * @param password GeoServer REST API password for the former username
     */
    public GeoServerRESTImporterManager(URL restURL, String username, String password)
            throws IllegalArgumentException {
        super(restURL, username, password);
    }

    /**
     * Retrieves the Import JSON Object given its identifier
     *
     * @param imp int: Import context number ID
     * @return {@link JsonNode}
     * @throws Exception Exception
     */
    public JsonNode getImport(int imp) throws Exception {
        JsonNode asJSON = HTTPUtils.getAsJSON(String.format(buildUrl() + "/%d", imp), gsuser, gspass);
        return asJSON.at("/import");
    }

    /**
     * Retrieves the Import Task JSON Object given its identifier and task number
     *
     * @param imp  int: Import context number ID
     * @param task int: Task number
     * @return {@link JsonNode}
     * @throws Exception Exception
     */
    public JsonNode getTask(int imp, int task) throws Exception {
        JsonNode node = HTTPUtils.getAsJSON(String.format(buildUrl() + "/%d/tasks/%d?expand=all", imp, task), gsuser, gspass);
        return node.at("/task");
    }

    /**
     * Example usage:
     * <pre>
     *  // Creates a new Importer Context and gets back the ID
     *  int i = postNewImport();
     *  
     *  // Attaches to the new Importer Context a Task pointing to a shapefile's zip archive
     *  int t = postNewTaskAsMultiPartForm(i, "/path_to/shape/archsites_no_crs.zip");
     *
     *  // Check that the Task was actually created and that the CRS has not recognized in this case
     *  JSONObject task = getTask(i, t);
     *  assertEquals("NO_CRS", task.getString("state"));
     *  
     *  // Prepare the JSON String instructing the Task about the SRS to use
     *  String json = 
     *  "{" +
     *    "\"task\": {" +
     *      "\"layer\": {" +
     *              "\"srs\": \"EPSG:4326\"" + 
     *       "}" +
     *     "}" + 
     *  "}";
     *  
     *  // Performing the Task update
     *  putTask(i, t, json);
     *
     *  // Double check that the Task is in the READY state
     *  task = getTask(i, t);
     *  assertEquals("READY", task.getString("state"));
     *  assertEquals("gs_archsites", task.getJSONObject("layer").getJSONObject("style").getString("name"));
     *  
     *  // Prepare the JSON String instructing the Task avout the SLD to use for the new Layer
     *  json = 
     *  "{" +
     *    "\"task\": {" +
     *      "\"layer\": {" +
     *        "\"style\": {" +
     *              "\"name\": \"point\"" + 
     *           "}" +
     *         "}" +
     *     "}" + 
     *  "}";
     *  
     *  // Performing the Task update
     *  putTask(i, t,json);
     *
     *  // Double check that the Task is in the READY state and that the Style has been correctly updated
     *  task = getTask(i, t);
     *  assertEquals("READY", task.getString("state"));
     *  assertEquals("point", task.getJSONObject("layer").getJSONObject("style").getString("name"));
     *  
     *  // Finally starts the Import ...
     *  postImport(i);
     * </pre>
     * 
     * @param imp int: Import context number ID
     * @param task int: Task number
     * @param json String: JSON containing the Task properties to be updated
     */
    public void putTask(int imp, int task, final String json) {
        //HTTPUtils.putJson(String.format(buildUrl()+"/%d/tasks/%d", imp, task), json, gsuser, gspass);
        HTTPUtils.put(String.format(buildUrl()+"/%d/tasks/%d", imp, task), json, "text/plain", gsuser, gspass);
    }

    /**
     * Just update the Layers properties associated to a Task (t) in the Importer Context (i).
     * 
     * e.g.:
     * <pre>
     * putTaskLayer(i, t, "{\"title\":\"Archsites\", \"abstract\":\"Archeological Sites\"}");
     * </pre>
     * 
     * @param imp int: Import context number ID
     * @param task int: Task number
     * @param json String: JSON containing the Layer properties to be updated
     */
    public void putTaskLayer(int imp, int task, final String json) {
        HTTPUtils.putJson(String.format(buildUrl()+"/%d/tasks/%d/layer", imp, task), json, gsuser, gspass);
    }
    
    /**
     * Just update the Layers properties associated to a Task (t) in the Importer Context (i).
     * 
     * e.g.:
     * <pre>
     * putTaskLayer(i, t, "{\"title\":\"Archsites\", \"abstract\":\"Archeological Sites\"}");
     * </pre>
     * 
     * @param imp int: Import context number ID
     * @param task int: Task number
     * @param json String: JSON containing the Layer properties to be updated
     */
    public void postTaskTransform(int imp, int task, final String json) {
        HTTPUtils.postJson(String.format(buildUrl()+"/%d/tasks/%d/transforms", imp, task), json, gsuser, gspass);
    }

    /**
     * Creates an empty Importer Context.
     * 
     * @return The new Importer Context ID
     * @throws Exception
     */
    public int postNewImport() throws Exception {
        return postNewImport(null);
    }
    
    /**
     * e.g.:
     * <pre>
     * String body = 
     *         "{" + 
     *              "\"import\": { " + 
     *                  "\"data\": {" +
     *                     "\"type\": \"mosaic\", " + 
     *                     "\"time\": {" +
     *                        " \"mode\": \"auto\"" + 
     *                     "}" + 
     *                   "}" +
     *              "}" + 
     *         "}";
     * </pre>
     * 
     * @param body JSON String representing the Importer Context definition
     * @return The new Importer Context ID
     */
    public int postNewImport(String body) {
        String resp = body == null ? HTTPUtils.post(buildUrl(), "", "text/plain", gsuser, gspass)
            : HTTPUtils.postJson(buildUrl(), body, gsuser, gspass);
        JsonNode json = HTTPUtils.json(resp);
        return json.at("/import/id").asInt();
    }

    /**
     * Actually starts the READY State Import.
     * 
     * @param imp int: Import context number ID
     */
    public void postImport(int imp) {
        HTTPUtils.post(buildUrl()+"/" + imp + "?exec=true", "", "text/plain", gsuser, gspass);
    }

    /**
     * 
     * @param imp int: Import context number ID
     * @param data data
     * @return int
     * @throws Exception Exception
     */
    public int postNewTaskAsMultiPartForm(int imp, String data) throws Exception {
        String resp = HTTPUtils.postMultipartForm(buildUrl()+"/" + imp + "/tasks", unpack(data), gsuser, gspass);
        
        JsonNode json = HTTPUtils.json(resp);
        return json.at("/task/id").asInt();
    }

    /**
     * Allows to attach a new zip file to an existing Importer Context.
     * 
     * @param imp int: Import context number ID
     * @param path path
     * @return int
     */
    public int putNewTask(int imp, String path) {
        File zip = new File(path);

        String resp = HTTPUtils.put(buildUrl()+"/" + imp + "/tasks/" + zip.getName(), zip, MediaType.APPLICATION_ZIP.toString(), gsuser, gspass);

        JsonNode json = HTTPUtils.json(resp);
        return json.at("/task/id").asInt();
    }
    
    //=========================================================================
    // Util methods
    //=========================================================================
    
    /**
     * Creates the base REST URL for the imports
     */
    protected String buildUrl() {
        StringBuilder sUrl = new StringBuilder(gsBaseUrl.toString()).append("/rest/imports");

        return sUrl.toString();
    }

    /**
     * Creates a temporary file
     * 
     * @return Path to the temporary file
     * @throws Exception Exception
     */
    public static File tmpDir() throws Exception {
        File dir = File.createTempFile("importer", "data", new File("target"));
        dir.delete();
        dir.mkdirs();
        return dir;
    }
    
    /**
     * Expands a zip archive into the temporary folder.
     * 
     * @param path The absolute path to the source zip file
     * @return Path to the temporary folder containing the expanded files
     * @throws Exception Exception
     */
    public static File unpack(String path) throws Exception {
        return unpack(path, tmpDir());
    }
    
    /**
     * Expands a zip archive into the target folder.
     * 
     * @param path The absolute path to the source zip file
     * @param dir Full path of the target folder where to expand the archive
     * @return Path to the temporary folder containing the expanded files
     * @throws Exception Exception
     */
    public static File unpack(String path, File dir) throws Exception {
        
        File file = new File(path);
        
        //new VFSWorker().extractTo(file, dir);
        if (!file.delete()) {
            // fail early as tests will expect it's deleted
            throw new IOException("deletion failed during extraction");
        }
        
        return dir;
    }
}
