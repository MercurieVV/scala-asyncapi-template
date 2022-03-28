package {{ params['userScalaPackage'] }}.model;

{% if message.description() or message.examples()%}/**{% for line in message.description() | splitByLines %}
 * {{ line | safe}}{% endfor %}{% if message.examples() %}
 * Examples: {{message.examples() | examplesToString | safe}}{% endif %}
 */{% endif %}
final case class {{messageName | camelCase | upperFirst}} (
    {%- if message.payload().anyOf() or message.payload().oneOf() %}
        {%- set payloadName = 'OneOf' %}{%- set hasPrimitive = false %}
        {%- for obj in message.payload().anyOf() %}
            {%- set hasPrimitive = hasPrimitive or obj.type() !== 'object' %}
            {%- set payloadName = payloadName + obj.uid() | camelCase | upperFirst %}
        {%- endfor %}
        {%- for obj in message.payload().oneOf() %}
            {%- set hasPrimitive = hasPrimitive or obj.type() !== 'object' %}
            {%- set payloadName = payloadName + obj.uid() | camelCase | upperFirst %}
        {%- endfor %}
        {%- if hasPrimitive %}
            {%- set payloadName = 'Object' %}
        {%- else %}

    trait {{payloadName}} {

    }
        {%- endif %}
    {%- elif message.payload().allOf() %}
        {%- set payloadName = 'AllOf' %}
        {%- for obj in message.payload().allOf() %}
            {%- set payloadName = payloadName + obj.uid() | camelCase | upperFirst %}
        {%- endfor %}
    public class {{payloadName}} {
        {%- for obj in message.payload().allOf() %}
            {%- set varName = obj.uid() | camelCase %}
            {%- set className = obj.uid() | camelCase | upperFirst %}
            {%- set propType = obj | defineType(obj.uid()) | safe %}

            {%- if obj.type() === 'array' %}
                {%- set varName = obj.uid() | camelCase + 'List' %}
            {%- endif %}
        private @Valid {{propType}} {{varName}};

        public {{propType}} get{{className}}() {
            return {{varName}};
        }

        public void set{{className}}({{propType}} {{varName}}) {
            this.{{varName}} = {{varName}};
        }
        {%- endfor %}
    }
    {% else %}
        {%- set payloadName = message.payload().uid() | camelCase | upperFirst %}
    {%- endif %}
    private {{payloadName}} payload;
)