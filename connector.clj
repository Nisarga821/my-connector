(config
    (text-field
        :name        "apikey"
        :label       "username"
        :placeholder "Enter your email address"
        :required    true
    )
  (password-field
    :name        "apiKey"
    :label       "API Key"
    :placeholder "api_xxxx.xxxxxxxxxxxxxxxx"
    :required    true
    :description "Generate your API key from Close: Settings → Developer → API Keys."
    (api-config-field :masked true)
  )
)
(default-source
  (http/get
    :base-url "https://api.close.com/api/v1"         

    (query-params
      "_limit"  100)                                 

    (header-params
      "Accept"       "application/json"
      "Content-Type" "application/json")
  )
  (auth/http-basic                                   
    :username "{apiKey}"
    :password "")

  (paging/no-pagination)                             

  (error-handler
    (when :status 400 :action fail)                   
    (when :status 401 :action refresh)                
    (when :status 403 :action fail)                   
                    
  )
  (format/json)                                       
)
(temp-entity organization
  (api-docs-url "https://developer.close.com/resources/organizations/")

  (source
    (http/get :url "/organization/")        
    (extract-path "data")
    (paging/no-pagination)                  
    (setup-test
      (upon-receiving
        :code 200 (pass)
        :code 401 (fail "Invalid API Key. Please check your credentials.")
        :code 403 (fail "Access forbidden. Ensure your API key has the right permissions.")
        :code 429 (fail "Too many requests. Please try again later."))
      :running-default-message "Verifying connection to Close CRM..."
      :failure-default-message "Failed to connect to Close CRM. Please check your API Key.")
  )
  (fields
    id           :id                     
    name 
  )
)

(entity LEAD
  (api-docs-url "https://developer.close.com/resources/leads/")

  (source
    (http/get :url "/lead/")
    (extract-path "data")
    (paging/has-more
      :has-more-path "has_more"
      :limit-param   "_limit"
      :offset-param  "_skip"
      :page-size     100)
    (setup-test
      (upon-receiving
        :code 200 (pass)
        :code 401 (fail "Invalid API Key. Please check your credentials.")
        :code 403 (fail "Access forbidden. Check API key permissions.")
        :code 429 (fail "Too many requests. Please try again later."))
      :running-default-message "Connecting to Close CRM /lead/..."
      :failure-default-message "Failed to connect to Close CRM /lead/.")
  )
  (fields
    id              :id                
    name                                
    display_name                        
    description                         
    html_url                            
    status_id                          
    status_label                       
    organization_id                     
    updated_by                          
    date_created    
    date_updated    
  )
)


(entity LEAD_CONTACT
  (api-docs-url "https://developer.close.com/resources/contacts/")

  (fields
    id              :id                 
    name                                
    display_name                        
    title                              
    organization_id                    
    updated_by                         
    date_created            
  )
)


(entity CONTACT_EMAIL
  (fields
    email                               
    type                                
    is_unsubscribed                     
  )
)


(entity CONTACT_PHONE
  (fields
    phone                               
    phone_formatted                     
    type                                
  )
)


(entity lead_opportunity
  
  (api-docs-url "https://developer.close.com/resources/opportunities/")

  (fields
    id                      :id         
    lead_id                            
    lead_name                           
    note                                
    status_id                          
    status_label                        
    status_type                         
    pipeline_id                         
    pipeline_name                       
    user_id                             
    user_name                           
    organization_id                    
    value                               
    value_period                        
    value_currency                      
    value_formatted                     
    annualized_value                   
    annualized_expected_value           
    expected_value                      
    confidence                          
    date_created            
    date_updated            
  )
)

(entity CONTACTS
  (api-docs-url "https://developer.close.com/resources/contacts/")

  (source
    (http/get 
      :base-url "https://api.close.com/api/v1"
      :url "/contact/"
    )
    (auth/http-basic)
    (paging/no-pagination)
    (format/json)
    (extract-path "data")
  )

  (fields
    id :id
    name
    display_name
    organization_id
    title
    created_by
    updated_by
    date_created
    date_updated
  )
)

(entity CONTACT_EMAIL
  (fields
    contact_id :<= "id"
    email :<= "email"
    type :<= "type"
    is_unsubscribed :<= "is_unsubscribed"
  )

  (relate
    (needs CONTACTS :prop "contact_id")
  )
)

(entity CONTACT_PHONES
  (fields
    contact_id :<= "id"
    phone :<= "phone"
    type :<= "type"
    country :<= "country"
    phone_formatted :<= "phone_formatted"
    is_unsubscribed :<= "is_unsubscribed"
  )

  (relate
    (needs CONTACTS :prop "contact_id")
  )
)

(entity OPPORTUNITIES
  (api-docs-url "https://developer.close.com/resources/opportunities/")

  (source
    (http/get
      :base-url "https://api.close.com/api/v1"
      :url "/opportunity/"
    )
    (auth/http-basic)
    (format/json)
    (paging/no-pagination)
    (extract-path "data")
  )

  (fields
    id :id
    lead_id
    lead_name
    organization_id
    user_id
    user_name
    pipeline_id
    pipeline_name
    status_id
    status_label
    status_type
    value
    value_period
    value_currency
    value_formatted
    expected_value
    annualized_value
    annualized_expected_value
    confidence
    note
    date_created
    date_updated
  )
)

(entity TASKS

  (fields
    id :<= "id"
    type :<= "_type"
    assigned_to :<= "assigned_to"
    assigned_to_name :<= "assigned_to_name"
    created_by :<= "created_by"
    created_by_name :<= "created_by_name"
    updated_by :<= "updated_by"
    updated_by_name :<= "updated_by_name"

    lead_id :<= "lead_id"
    lead_name :<= "lead_name"
    contact_id :<= "contact_id"
    contact_name :<= "contact_name"
    organization_id :<= "organization_id"

    object_id :<= "object_id"
    object_type :<= "object_type"

    date :<= "date"
    date_created :<= "date_created"
    date_updated :<= "date_updated"

    is_complete :<= "is_complete"
    is_dateless :<= "is_dateless"

    text :<= "text"
    subject :<= "subject"
    body_preview :<= "body_preview"

    phone :<= "phone"
    phone_formatted :<= "phone_formatted"
    phone_number_description :<= "phone_number_description"
    local_phone :<= "local_phone"

    voicemail_duration :<= "voicemail_duration"
    voicemail_url :<= "voicemail_url"

    recording_url :<= "recording_url"

    opportunity_note :<= "opportunity_note"
    opportunity_value :<= "opportunity_value"
    opportunity_value_currency :<= "opportunity_value_currency"
    opportunity_value_formatted :<= "opportunity_value_formatted"
    opportunity_value_period :<= "opportunity_value_period"

    view :<= "view"
  )
)

(entity TASK_ATTACHMENTS

  (fields
    task_id :<= "id"
    content_type :<= "content_type"
    filename :<= "filename"
    media_id :<= "media_id"
    size :<= "size"
    thumbnail_url :<= "thumbnail_url"
    url :<= "url"
  )

  (relate
    (needs TASKS :prop "task_id")
  )
)
