package {{ params['userScalaPackage'] }}.service;
{%- from "partials/CommonPublisher.scala" import commonPublisher -%}
{%- from "partials/KafkaPublisher.scala" import kafkaPublisher -%}
{%- if asyncapi | isProtocol('kafka') -%}
{{- kafkaPublisher(asyncapi, params) -}}
{%- else -%}
{{- commonPublisher(asyncapi) -}}
{%- endif -%}