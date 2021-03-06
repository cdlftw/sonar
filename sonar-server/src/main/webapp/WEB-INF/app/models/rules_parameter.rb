#
# Sonar, entreprise quality control tool.
# Copyright (C) 2008-2012 SonarSource
# mailto:contact AT sonarsource DOT com
#
# Sonar is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 3 of the License, or (at your option) any later version.
#
# Sonar is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with {library}; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
#
class RulesParameter < ActiveRecord::Base
  include RulesConfigurationHelper

  PARAM_MAX_NUMBER = 4

  validates_presence_of :name, :param_type
  belongs_to :rule

  def is_set_type
    param_type.at(1) == "[" && param_type.ends_with?("]")
  end

  def get_allowed_tokens
    param_type[2, param_type.length-3].split(",")
  end

  # We can provide the rule as parameter to avoid reloading this rule_parameter's rule.
  # This hack would be useless if we could use :inverse_of on :rule
  def description(param_rule = rule)
    @l10n_description ||=
      begin
        result = Java::OrgSonarServerUi::JRubyFacade.getInstance().getRuleParamDescription(I18n.locale, param_rule.repository_key, param_rule.plugin_rule_key, name())
        result = read_attribute(:description) unless result
        result
      end
  end

  def description=(value)
    write_attribute(:description, value)
  end

  def validate_value(attribute, errors, value)
    validate_rule_param(attribute, param_type, errors, value)
  end

  def to_hash_json(active_rule)
    json = {'name' => name}
    json['description']=description if description
    if active_rule
      active_parameter = active_rule.active_param_by_param_id(id)
      json['value'] = active_parameter.value if active_parameter
    end
    json
  end

  def to_xml(active_rule, xml)
    xml.param do
      xml.name(name)
      xml.description { xml.cdata!(description) } if description
      if active_rule
        active_parameter = active_rule.active_param_by_param_id(id)
        xml.value(active_parameter.value) if active_parameter
      end
    end
  end

  def <=>(other)
    name <=> other.name
  end
end
