package com.tweetgeister.web.framework;

import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.spi.template.ViewProcessor;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.UUID;

/**
 * Basic Freemarker ViewProcessor implementation based on Freemarker TemplateProcessor implementation which is
 * deprecated
 */
@Provider
public class FreemarkerViewProcessor implements ViewProcessor<Template> {

    private static final Logger LOGGER = Logger.getLogger(FreemarkerViewProcessor.class);

    private static final String UNIQ_VARNAME = "__uniq__";

    private Configuration _conf;

    /**
     * 
     */
    public FreemarkerViewProcessor() {
        _conf = new Configuration();

        _conf.setSharedVariable("uniq", new TemplateScalarModel() {
            public String getAsString() throws TemplateModelException {
                Environment env = Environment.getCurrentEnvironment();
                synchronized (env) {
                    SimpleScalar templateModel = (SimpleScalar) env.getVariable(UNIQ_VARNAME);
                    if (templateModel == null) {
                        UUID uuid = UUID.randomUUID();
                        String uniqueId = String.format("id%s",
                                Math.abs(uuid.getMostSignificantBits() ^ uuid.getLeastSignificantBits()));
                        env.setVariable(UNIQ_VARNAME, templateModel = new SimpleScalar(uniqueId));
                    }
                    return templateModel.getAsString();
                }
            }
        });
    }

    /**
     * Setup location of freemarker templates
     * 
     * @param servletContext
     */
    @Context
    public void setServletContext(ServletContext servletContext) {
        _conf.setServletContextForTemplateLoading(servletContext, "/WEB-INF/freemarker");
        _conf.setTemplateUpdateDelay(0);
    }

    /**
     * {@inheritDoc}
     */
 
    public Template resolve(String name) {

        try {
            return _conf.getTemplate(name);
        } catch (final IOException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */

    public void writeTo(Template template, Viewable viewable, OutputStream outputStream) throws IOException {

        if (template == null) {
            throw new IllegalArgumentException("Template is required to write. " + viewable.getTemplateName());
        }

        outputStream.flush();
        try {
            template.process(viewable.getModel(), new OutputStreamWriter(outputStream));
        } catch (final TemplateException e) {
            LOGGER.error(String.format("error processing template [%s]", template.getName()), e);
        }
    }
}
