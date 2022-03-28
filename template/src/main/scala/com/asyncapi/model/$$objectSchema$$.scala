package {{ params['userScalaPackage'] }}.model;

import javax.validation.constraints.*;
import javax.validation.Valid;

import java.util.List;
import java.util.Objects;

{% if schema.description() or schema.examples() %}/**{% for line in schema.description() | splitByLines %}
 * {{ line | safe}}{% endfor %}{% if schema.examples() %}
 * Examples: {{schema.examples() | examplesToString | safe}}{% endif %}
 */{% endif %}
final case class {{schemaName | camelCase | upperFirst}} (
    {% for propName, prop in schema.properties() %}
        {%- set isRequired = propName | isRequired(schema.required()) %}
        {%- if prop.type() === 'object' %}
    {{prop.uid() | camelCase | upperFirst}} {{propName | camelCase}};
        {%- elif prop.type() === 'array' %}
            {%- if prop.items().type() === 'object' %}
    List[{{prop.items().uid() | camelCase | upperFirst}}] {{propName | camelCase}}List;
            {%- elif prop.items().format() %}
    List[{{prop.items().format() | toScalaType | toClass}}] {{propName | camelCase}}List;
            {%- else %}
    List[{{prop.items().type() | toScalaType | toClass}}] {{propName | camelCase}}List;
            {%- endif %}
        {%- elif prop.enum() and (prop.type() === 'string' or prop.type() === 'integer') %}
    enum {{propName | camelCase | upperFirst}}Enum({% if prop.type() === 'string'%}String{% else %}Int{% endif %} v) {
            {% for e in prop.enum() %}
                {%- if prop.type() === 'string'%}
        case {{e | upper | createEnum}}({{e}}){% if not loop.last %},{% else %};{% endif %}
                {%- else %}
        NUMBER_{{e}}({{e}}){% if not loop.last %},{% else %};{% endif %}
                {%- endif %}
            {% endfor %}
    }

    {{propName | camelCase | upperFirst}}Enum {{propName | camelCase}};
        {%- elif prop.anyOf() or prop.oneOf() %}
            {%- set propType = 'OneOf' %}{%- set hasPrimitive = false %}
            {%- for obj in prop.anyOf() %}
                {%- set hasPrimitive = hasPrimitive or obj.type() !== 'object' %}
                {%- set propType = propType + obj.uid() | camelCase | upperFirst %}
            {%- endfor %}
            {%- for obj in prop.oneOf() %}
                {%- set hasPrimitive = hasPrimitive or obj.type() !== 'object' %}
                {%- set propType = propType + obj.uid() | camelCase | upperFirst %}
            {%- endfor %}
            {%- if hasPrimitive %}
                {%- set propType = 'Object' %}
            {%- else %}
    trait {{propType}} {

    }
            {%- endif %}
    {{propType}} {{propName | camelCase}};
        {%- elif prop.allOf() %}
            {%- set allName = 'AllOf' %}
            {%- for obj in prop.allOf() %}
                {%- set allName = allName + obj.uid() | camelCase | upperFirst %}
            {%- endfor %}
    case class {{allName}} {
            {%- for obj in prop.allOf() %}
                {%- set varName = obj.uid() | camelCase %}
                {%- set className = obj.uid() | camelCase | upperFirst %}
                {%- set propType = obj | defineType(obj.uid()) | safe %}

                {%- if obj.type() === 'array' %}
                    {%- set varName = obj.uid() | camelCase + 'List' %}
                {%- endif %}
        {{propType}} {{varName}};
            {%- endfor %}
    }

    {{allName}} {{propName | camelCase}};
        {%- else %}
            {%- if prop.format() %}
    {{prop.format() | toScalaType(isRequired)}} {{propName | camelCase}};
            {%- else %}
    {{prop.type() | toScalaType(isRequired)}} {{propName | camelCase}};
            {%- endif %}
        {%- endif %}
    {% endfor %}

    {% for propName, prop in schema.properties() %}
        {%- set varName = propName | camelCase %}
        {%- set className = propName | camelCase | upperFirst %}
        {%- set propType = prop | defineType(propName) | safe %}

        {%- if prop.type() === 'array' %}
            {%- set varName = propName | camelCase + 'List' %}
        {%- endif %}

    {% if prop.description() or prop.examples()%}/**{% for line in prop.description() | splitByLines %}
     * {{ line | safe}}{% endfor %}{% if prop.examples() %}
     * Examples: {{prop.examples() | examplesToString | safe}}{% endif %}
     */{% endif %}
    @JsonProperty("{{propName}}")
    {%- if propName | isRequired(schema.required()) %}@NotNull{% endif %}
    {%- if prop.minLength() or prop.maxLength() or prop.maxItems() or prop.minItems() %}@Size({% if prop.minLength() or prop.minItems() %}min = {{prop.minLength()}}{{prop.minItems()}}{% endif %}{% if prop.maxLength() or prop.maxItems() %}{% if prop.minLength() or prop.minItems() %},{% endif %}max = {{prop.maxLength()}}{{prop.maxItems()}}{% endif %}){% endif %}    
    {%- if prop.pattern() %}@Pattern(regexp="{{prop.pattern() | addBackSlashToPattern}}"){% endif %}
    {%- if prop.minimum() %}@Min({{prop.minimum()}}){% endif %}{% if prop.exclusiveMinimum() %}@Min({{prop.exclusiveMinimum() + 1}}){% endif %}
    {%- if prop.maximum() %}@Max({{prop.maximum()}}){% endif %}{% if prop.exclusiveMaximum() %}@Max({{prop.exclusiveMaximum() + 1}}){% endif %}
    {% endfor %}
)