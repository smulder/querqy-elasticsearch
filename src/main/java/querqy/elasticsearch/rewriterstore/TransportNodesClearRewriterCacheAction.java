package querqy.elasticsearch.rewriterstore;

import org.elasticsearch.action.FailedNodeException;
import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.action.support.nodes.TransportNodesAction;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.indices.IndicesService;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;
import querqy.elasticsearch.RewriterShardContexts;

import java.util.List;
import java.util.Optional;

public class TransportNodesClearRewriterCacheAction extends TransportNodesAction<NodesClearRewriterCacheRequest,
        NodesClearRewriterCacheResponse, NodesClearRewriterCacheRequest.NodeRequest, NodesClearRewriterCacheResponse.NodeResponse> {

    protected RewriterShardContexts rewriterShardContexts;


    @Inject
    public TransportNodesClearRewriterCacheAction(final ThreadPool threadPool, final ClusterService clusterService,
                                              final TransportService transportService,
                                              final ActionFilters actionFilters,
                                              final IndicesService indexServices,
                                              final Client client,
                                              final RewriterShardContexts rewriterShardContexts) {

        super(NodesClearRewriterCacheAction.NAME, threadPool, clusterService, transportService, actionFilters,
                NodesClearRewriterCacheRequest::new, NodesClearRewriterCacheRequest.NodeRequest::new,
                ThreadPool.Names.MANAGEMENT, NodesClearRewriterCacheResponse.NodeResponse.class);
        this.rewriterShardContexts = rewriterShardContexts;
    }


    @Override
    protected NodesClearRewriterCacheResponse newResponse(final NodesClearRewriterCacheRequest request,
                                                          final List<NodesClearRewriterCacheResponse.NodeResponse>
                                                                  nodeResponses,
                                                          final List<FailedNodeException> failures) {
        return new NodesClearRewriterCacheResponse(clusterService.getClusterName(), nodeResponses, failures);
    }

    @Override
    protected NodesClearRewriterCacheRequest.NodeRequest newNodeRequest(final String nodeId,
                                                                        final NodesClearRewriterCacheRequest request) {

        return request.newNodeRequest(nodeId);
    }

    @Override
    protected NodesClearRewriterCacheResponse.NodeResponse newNodeResponse() {
        return new NodesClearRewriterCacheResponse.NodeResponse();
    }

    @Override
    protected NodesClearRewriterCacheResponse.NodeResponse nodeOperation(
            final NodesClearRewriterCacheRequest.NodeRequest request) {

        final Optional<String> rewriterId = request.getRewriterId();
        if (rewriterId.isPresent()) {
            rewriterId.ifPresent(rewriterShardContexts::clearRewriter);
        } else {
            rewriterShardContexts.clearRewriters();
        }

        return new NodesClearRewriterCacheResponse.NodeResponse(clusterService.localNode());


    }
}
