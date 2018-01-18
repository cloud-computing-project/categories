# RSO: Categories microservice

## Prerequisites

```bash
docker run -d --name categories -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=categorie -p 5433:5432 postgres:latest
```

## Run application in Docker

```bash
docker run -p 8087:8087 ejmric/categories
```

## Travis status 
[![Build Status](https://travis-ci.org/cloud-computing-project/categories.svg?branch=master)](https://travis-ci.org/cloud-computing-project/categories)