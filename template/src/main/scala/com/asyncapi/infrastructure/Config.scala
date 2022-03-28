package {{ params['userScalaPackage'] }}.infrastructure;

{%- from "partials/AmqpConfig.scala" import amqpConfig -%}
{%- from "partials/MqttConfig.scala" import mqttConfig -%}
{%- from "partials/KafkaConfig.scala" import kafkaConfig -%}

{%- if asyncapi | isProtocol('amqp') -%}
{{- amqpConfig(asyncapi, params) -}}
{%- endif -%}
{%- if asyncapi | isProtocol('mqtt') -%}
{{- mqttConfig(asyncapi, params) -}}
{%- endif -%}
{%- if (asyncapi | isProtocol('kafka')) or (asyncapi | isProtocol('kafka-secure')) -%}
{{- kafkaConfig(asyncapi, params) -}}
{%- endif -%}