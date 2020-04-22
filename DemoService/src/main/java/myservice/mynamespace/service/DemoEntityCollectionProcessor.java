package myservice.mynamespace.service;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;

import com.sun.tools.javac.util.List;

public class DemoEntityCollectionProcessor implements EntityCollectionProcessor {

	private OData odata;
	private ServiceMetadata serviceMetadata;

	@Override
	public void init(OData odata, ServiceMetadata serviceMetadata) {
		
		this.odata = odata;
		this.serviceMetadata = serviceMetadata;

	}

	@Override
	public void readEntityCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo,
			ContentType responseFormat) throws ODataApplicationException, ODataLibraryException, SerializerException {
		
		
		List<UriResource> resourcePaths = (List<UriResource>) uriInfo.getUriResourceParts();
		UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
		EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
		
		EntityCollection entitySet = getData(edmEntitySet);
		
		ODataSerializer serializer = odata.createSerializer(responseFormat);
		
		EdmEntityType edmEntityType = edmEntitySet.getEntityType();
		ContextURL contextURL = ContextURL.with().entitySet(edmEntitySet).build();
		
		final String id = request.getRawBaseUri() + "/" + edmEntitySet.getName();
		EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with().id(id).contextURL(contextURL).build();
		SerializerResult serializerResult = serializer.entityCollection(serviceMetadata, edmEntityType, entitySet, opts);
		InputStream serilizedContent = serializerResult.getContent();
		
		response.setContent(serilizedContent);
		response.setStatusCode(HttpStatusCode.OK.getStatusCode());
		response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());

	}

	private EntityCollection getData(EdmEntitySet edmEntitySet) {
		
		EntityCollection productsCollection = new EntityCollection();
		
		if (DemoEdmProvider.ES_PRODUCTS_NAME.equals(edmEntitySet.getName())) {
			List<Entity> productList = (List<Entity>) productsCollection.getEntities();
			
			final Entity e1 = new Entity();
			e1.addProperty(new Property(null,"ID", ValueType.PRIMITIVE, 1));
			e1.addProperty(new Property(null,"Name", ValueType.PRIMITIVE, "Notebook"));
			e1.addProperty(new Property(null,"Description", ValueType.PRIMITIVE, "Notebook Basic, 1.7GHz - 15 XGA - 1024MB DDR2 SDRAM - 40GB"));
			e1.setId(createId("Products",1));
			productList.add(e1);
			
			final Entity e2 = new Entity();
			e2.addProperty(new Property(null,"ID", ValueType.PRIMITIVE, 2));
			e2.addProperty(new Property(null,"Name", ValueType.PRIMITIVE, "1UMTS PDA"));
			e2.addProperty(new Property(null,"Description", ValueType.PRIMITIVE, "Ultrafast 3G UMTS/HSDPA Pocket PC, supports GSM network"));
			e2.setId(createId("Products",1));
			productList.add(e2);
			
			final Entity e3 = new Entity();
			e3.addProperty(new Property(null,"ID", ValueType.PRIMITIVE, 3));
			e3.addProperty(new Property(null,"Name", ValueType.PRIMITIVE, "Ergo Screen"));
			e3.addProperty(new Property(null,"Description", ValueType.PRIMITIVE, "19 Optimum Resolution 1024 x 768 @ 85Hz, resolution 1280 x 960"));
			e3.setId(createId("Products",1));
			productList.add(e3);
		}
		
		return productsCollection;
	}

	private URI createId(String entitySetName, Object id) {
		try {
			return new URI(entitySetName + "(" + String.valueOf(id) + ")");
		} catch(URISyntaxException e) {
			throw new ODataRuntimeException("Unable to create id for entity: " + entitySetName, e);
		}
		
	}

}
