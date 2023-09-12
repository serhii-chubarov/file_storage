ifdef OS
    tomcat_start = catalina.bat start
    tomcat_stop = catalina.bat stop
else
    tomcat_start = catalina.sh start
    tomcat_stop = catalina.sh stop
endif

API_VERSION=v1

tomcat_start tu:
	$(tomcat_start)

tomcat_stop td:
	$(tomcat_stop)

verify v:
	mvn clean verify

deploy:
	mvn clean tomcat7:deploy

rt:
	mvn clean tomcat7:redeploy

redeploy r:
	mvn clean tomcat7:redeploy -DskipTests

flyway_clean fc:
	mvn flyway:clean

flyway_info fi:
	mvn flyway:info

flyway_migrate fm:
	mvn flyway:migrate

create cu:
	curl -X POST -v http://localhost:8080/file_storage/api/${API_VERSION}/users -H 'Content-type:application/json' -d '{"name":"alice"}'

cf:
	curl -X POST -v http://localhost:8080/file_storage/api/${API_VERSION}/files -H 'Content-type:application/json' -H 'user-id:1' -d '{"name":"file_1", "data":"abcdefg"}'
#	curl -X POST -v http://localhost:8080/file_storage/api/${API_VERSION}/files -H 'Content-type:application/json' -H 'user-id:2' -d '{"name":"file_2", "data":"abcdefg"}'

update uu:
	curl -X PUT -v http://localhost:8080/file_storage/api/${API_VERSION}/users/1 -H 'Content-type:application/json' -d '{"name":"bob"}'

uf:
	curl -X PUT -v http://localhost:8080/file_storage/api/${API_VERSION}/files/1 -H 'Content-type:application/json' -d '{"name":"file_1", "data":"buba"}'

find fu:
	curl -X GET -v http://localhost:8080/file_storage/api/${API_VERSION}/users | json_pp
#	curl -X GET -v http://localhost:8080/file_storage/api/${API_VERSION}/users/1 | json_pp

fe:
	curl -X GET -v http://localhost:8080/file_storage/api/${API_VERSION}/events | json_pp
#	curl -X GET -v http://localhost:8080/file_storage/api/${API_VERSION}/events/1 | json_pp

ff:
	curl -X GET -v http://localhost:8080/file_storage/api/${API_VERSION}/files | json_pp
#	curl -X GET -v http://localhost:8080/file_storage/api/${API_VERSION}/files/1 | json_pp

delete d:
	curl -X DELETE -v http://localhost:8080/file_storage/api/${API_VERSION}/users/1

df:
	curl -X DELETE -v http://localhost:8080/file_storage/api/${API_VERSION}/files/1

errors:
	curl http://localhost:8080/file_storage/foobar | json_pp
	curl -X POST http://localhost:8080/file_storage/foobar | json_pp
