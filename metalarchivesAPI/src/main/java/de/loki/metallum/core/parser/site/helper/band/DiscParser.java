package de.loki.metallum.core.parser.site.helper.band;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.loki.metallum.core.util.MetallumUtil;
import de.loki.metallum.core.util.net.MetallumURL;
import de.loki.metallum.core.util.net.downloader.Downloader;
import de.loki.metallum.entity.Disc;
import de.loki.metallum.enums.DiscType;

public final class DiscParser {
	private final Document	doc;

	public DiscParser(final long bandId) throws ExecutionException {
		this.doc = Jsoup.parse(Downloader.getHTML(MetallumURL.assembleDiscographyURL(bandId)));
	}

	public final Disc[] parse() {
		List<Disc> discography = new LinkedList<Disc>();
		Elements rows = this.doc.getElementsByTag("tr");
		for (int i = 1; i < rows.size(); i++) {
			Element row = rows.get(i);
			Elements cols = row.getElementsByTag("td");
			Disc disc = new Disc();
			disc.setId(parseDiscId(cols.first()));
			disc.setName(cols.first().text());
			disc.setDiscType(parseDiscType(cols.get(1)));
			disc.setReleaseDate(cols.get(2).text());
			disc.setHasReview(parseReview(cols.get(3)));
			discography.add(disc);
		}
		return discography.toArray(new Disc[discography.size()]);
	}

	private final boolean parseReview(final Element element) {
		String content = element.text();
		content = MetallumUtil.trimNoBreakSpaces(content);
		return !content.isEmpty();
	}

	private final long parseDiscId(final Element element) {
		Element linkElem = element.getElementsByTag("a").first();
		String link = linkElem.attr("href");
		String id = link.substring(link.lastIndexOf("/") + 1, link.length());
		return Long.parseLong(id);
	}

	private final DiscType parseDiscType(final Element element) {
		final String discType = element.text();
		return DiscType.getTypeDiscTypeForString(discType);
	}
}
