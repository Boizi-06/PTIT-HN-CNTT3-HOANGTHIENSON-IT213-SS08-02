package com.rikkei.crm.splitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Splitter phân tách tài liệu dựa trên cấu trúc đề mục phân cấp Markdown (#, ##, ###).
 * Đặc biệt tối ưu cho Tài liệu Loại B (Quy chế, chính sách nhiều chương/điều).
 *
 * Cơ chế bảo toàn ngữ cảnh:
 * - Kế thừa cây tiêu đề cha con (Header Breadcrumbs: H1 > H2 > H3).
 * - Mỗi chunk chứa toàn bộ nội dung của một Điều/Khoản kèm tiêu đề Chương trực thuộc.
 * - Tránh chia cắt giữa chừng các điều khoản pháp lý.
 */
public class HierarchicalMarkdownTextSplitter implements DocumentTransformer {

    private static final Logger log = LoggerFactory.getLogger(HierarchicalMarkdownTextSplitter.class);
    private static final Pattern HEADER_PATTERN = Pattern.compile("^(#{1,6})\\s+(.+)$", Pattern.MULTILINE);

    private final int maxTokens;
    private final int minSectionChars;
    private final boolean preserveParentHeader;

    public HierarchicalMarkdownTextSplitter(int maxTokens, int minSectionChars, boolean preserveParentHeader) {
        this.maxTokens = maxTokens;
        this.minSectionChars = minSectionChars;
        this.preserveParentHeader = preserveParentHeader;
    }

    @Override
    public List<Document> apply(List<Document> documents) {
        List<Document> resultChunks = new ArrayList<>();

        for (Document doc : documents) {
            String text = doc.getText();
            if (text == null || text.isBlank()) {
                continue;
            }

            List<SectionNode> sections = parseMarkdownHierarchy(text);
            for (SectionNode section : sections) {
                // Tạo nội dung hoàn chỉnh kèm breadcrumb tiêu đề
                StringBuilder chunkContent = new StringBuilder();
                if (preserveParentHeader && !section.headerPath.isEmpty()) {
                    chunkContent.append("<!-- BREADCRUMB: ").append(String.join(" > ", section.headerPath)).append(" -->\n");
                }
                chunkContent.append(section.content.trim());

                if (chunkContent.length() < minSectionChars && !resultChunks.isEmpty()) {
                    // Nếu section quá ngắn, gộp vào chunk trước đó để tránh rác dữ liệu
                    Document prevDoc = resultChunks.remove(resultChunks.size() - 1);
                    String mergedText = prevDoc.getText() + "\n\n" + chunkContent;
                    Map<String, Object> mergedMeta = new HashMap<>(prevDoc.getMetadata());
                    mergedMeta.put("merged_sections", true);
                    resultChunks.add(new Document(prevDoc.getId(), mergedText, mergedMeta));
                } else {
                    Map<String, Object> metadata = new HashMap<>(doc.getMetadata());
                    metadata.put("splitter_strategy", "HEADER_BASED_HIERARCHICAL");
                    metadata.put("heading_level", section.level);
                    metadata.put("heading_title", section.title);
                    metadata.put("header_hierarchy", String.join(" > ", section.headerPath));
                    metadata.put("chunk_char_length", chunkContent.length());

                    resultChunks.add(new Document(UUID.randomUUID().toString(), chunkContent.toString(), metadata));
                }
            }
        }

        log.debug("[HEADER-SPLITTER] Đã phân tách {} documents gốc thành {} hierarchical chunks.",
                documents.size(), resultChunks.size());
        return resultChunks;
    }

    private List<SectionNode> parseMarkdownHierarchy(String markdown) {
        List<SectionNode> nodes = new ArrayList<>();
        String[] lines = markdown.split("\\r?\\n");

        String currentH1 = "";
        String currentH2 = "";
        String currentH3 = "";
        StringBuilder currentContent = new StringBuilder();
        int currentLevel = 0;
        String currentTitle = "Phần mở đầu";

        for (String line : lines) {
            Matcher matcher = HEADER_PATTERN.matcher(line);
            if (matcher.matches()) {
                // Lưu lại section trước đó nếu có nội dung
                if (currentContent.length() > 0) {
                    List<String> path = buildHeaderPath(currentH1, currentH2, currentH3);
                    nodes.add(new SectionNode(currentLevel, currentTitle, path, currentContent.toString()));
                    currentContent.setLength(0);
                }

                int level = matcher.group(1).length();
                String title = matcher.group(2).trim();
                currentLevel = level;
                currentTitle = title;

                if (level == 1) {
                    currentH1 = title;
                    currentH2 = "";
                    currentH3 = "";
                } else if (level == 2) {
                    currentH2 = title;
                    currentH3 = "";
                } else if (level == 3) {
                    currentH3 = title;
                }

                currentContent.append(line).append("\n");
            } else {
                currentContent.append(line).append("\n");
            }
        }

        if (currentContent.length() > 0) {
            List<String> path = buildHeaderPath(currentH1, currentH2, currentH3);
            nodes.add(new SectionNode(currentLevel, currentTitle, path, currentContent.toString()));
        }

        return nodes;
    }

    private List<String> buildHeaderPath(String h1, String h2, String h3) {
        List<String> path = new ArrayList<>();
        if (!h1.isBlank()) path.add(h1);
        if (!h2.isBlank()) path.add(h2);
        if (!h3.isBlank()) path.add(h3);
        return path;
    }

    private static class SectionNode {
        int level;
        String title;
        List<String> headerPath;
        String content;

        SectionNode(int level, String title, List<String> headerPath, String content) {
            this.level = level;
            this.title = title;
            this.headerPath = headerPath;
            this.content = content;
        }
    }
}
