package com.example;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;
import yandex.cloud.api.search.v2.SearchQueryOuterClass;
import yandex.cloud.api.search.v2.SearchService.WebSearchRequest;
import yandex.cloud.api.search.v2.SearchService.WebSearchResponse;
import yandex.cloud.api.search.v2.WebSearchServiceGrpc;

public class SearchClient {

    public static void main(String[] args) throws InterruptedException {
        String host = "searchapi.api.cloud.yandex.net";
        int port = 443;

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter query: ");
        String query = scanner.nextLine();
        System.out.print("Enter API key: ");
        String apiKey = scanner.nextLine();
        scanner.close();

        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .useTransportSecurity()
                .build();

        try {
            callSearch(channel, query, apiKey);
        } finally {
            shutdownChannel(channel);
        }
    }

    private static void callSearch(ManagedChannel channel, String query, String apiKey) {
        Metadata headers = new Metadata();
        Metadata.Key<String> AUTH_KEY = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
        headers.put(AUTH_KEY, "Bearer " + apiKey);

        WebSearchServiceGrpc.WebSearchServiceBlockingStub stub = MetadataUtils.attachHeaders(WebSearchServiceGrpc.newBlockingStub(channel), headers);

        WebSearchRequest request = WebSearchRequest.newBuilder()
                .setQuery(
                        SearchQueryOuterClass.SearchQuery.newBuilder().setQueryText(query)
                                .setSearchType(SearchQueryOuterClass.SearchQuery.SearchType.SEARCH_TYPE_COM)
                ).build();
        try {
            WebSearchResponse response = stub.search(request);
            String rawData = response.getRawData().toStringUtf8();;
            System.out.println("Search response:\n" + rawData);

        } catch (StatusRuntimeException e) {
            System.err.println("RPC failed: " + e.getStatus() + " - " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    private static void shutdownChannel(ManagedChannel channel) throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}
