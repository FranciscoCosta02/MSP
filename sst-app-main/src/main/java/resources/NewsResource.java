package resources;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.JWTValidation;
import utils.NewsData;

import static java.time.Instant.now;


@Path("/news")
public class NewsResource {

    private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private final KeyFactory newsKeyFactory = datastore.newKeyFactory().setKind("News");

    private static final Logger LOG = Logger.getLogger(NewsResource.class.getName());
    private final Gson g = new Gson();
    public NewsResource() {
    }

    private TimestampValue nonIndexedTimeStamp(Timestamp data) {
        return TimestampValue.newBuilder(data).setExcludeFromIndexes(true).build();
    }

    private StringValue nonIndexedString(String data) {
        return StringValue.newBuilder(data).setExcludeFromIndexes(true).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getNewsPage(@Context HttpServletRequest request){

        String Headerid = request.getHeader("Authorization");
        Headerid = Headerid.substring("Bearer".length()).trim();
        Jws<Claims> jwt;
        try {
            jwt = JWTValidation.parseJwt(Headerid);
        }
        catch (NoSuchElementException e){
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Invalid token").build();
        }
        assert jwt != null;
        //Claims values = jwt.getBody();

        Key newsKey = newsKeyFactory.newKey("news");
        Transaction txn = datastore.newTransaction();
        try{
            Entity newsEnt = txn.get(newsKey);
            List<NewsData> fullNews;
            if(newsEnt!=null) {
                Timestamp createAt = newsEnt.getTimestamp("createAt");
                if(Timestamp.now().compareTo(createAt)>=0){
                    fullNews = getNews();
                }
                else{
                    return Response.ok(newsEnt.getString("news")).build();
                }
            }
            else{
                long code_expiration_time = 1000L * 60 * 60 * 12;//12horas
                Date date = Date.from(now().plusMillis(code_expiration_time));
                fullNews = getNews();
                newsEnt = Entity.newBuilder(newsKey)
                        .set("news",nonIndexedString(fullNews.toString()))
                        .set("createAt",nonIndexedTimeStamp(Timestamp.now()))
                        .set("expireAt", nonIndexedTimeStamp(Timestamp.of(date))).build();
                txn.put(newsEnt);
                txn.commit();
            }
            return Response.ok(g.toJson(fullNews)).build();
        }catch (Exception e){
            txn.rollback();
            LOG.warning("ERROR MSG: "+ e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("Error: Try again later").build();
        }
        finally{
            if (txn.isActive()) {
                txn.rollback();
            }
        }






    }

    private static List<NewsData> getNews()throws IOException{

        Document doc = Jsoup.connect("https://www.fct.unl.pt/noticias").get();
        LOG.info(doc.title());
        Elements newsHeadlines = doc.select("div.noticia-corpo"); //css queries
        Elements newsImages = doc.select("div.noticia-imagem span a img"); //css queries
        Elements newsLinks = doc.select("div.noticia-imagem a"); //css queries
        ListIterator<Element> headlineItr = newsHeadlines.listIterator();
        ListIterator<Element> imagesItr = newsImages.listIterator();
        ListIterator<Element> linksItr = newsLinks.listIterator();
        List<NewsData> fullNews = new ArrayList<>(newsHeadlines.size());
        while(headlineItr.hasNext() && imagesItr.hasNext()){
            fullNews.add( new NewsData(headlineItr.next().text(), imagesItr.next().attr("src"),
                    "https://www.fct.unl.pt" + linksItr.next().attr("href")) );
        }
        return fullNews;
    }


}
