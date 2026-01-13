FROM nginx:alpine

# Remove the default nginx config
RUN rm /etc/nginx/conf.d/default.conf

# Copy the template configuration
COPY ./default.conf.template /etc/nginx/templates/

EXPOSE 80

# Use the nginx docker entrypoint which handles template substitution
# Environment variables will be substituted into the template
CMD ["nginx", "-g", "daemon off;"]
