FROM docker.elastic.co/elasticsearch/elasticsearch:7.17.9
RUN bin/elasticsearch-plugin install analysis-nori
