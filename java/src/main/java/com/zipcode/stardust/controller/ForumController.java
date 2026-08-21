package com.zipcode.stardust.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zipcode.stardust.model.Comment;
import com.zipcode.stardust.model.Post;
import com.zipcode.stardust.model.Subforum;
import com.zipcode.stardust.model.User;
import com.zipcode.stardust.repository.CommentRepository;
import com.zipcode.stardust.repository.PostRepository;
import com.zipcode.stardust.repository.SubforumRepository;
import com.zipcode.stardust.repository.UserRepository;
import com.zipcode.stardust.service.ForumService;
import com.zipcode.stardust.service.PostLikeService;

@Controller
public class ForumController {

    @Autowired
    private SubforumRepository subforumRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ForumService forumService;

    @Autowired
    private PostLikeService postLikeService;

    @Value("${site.name:DIY Stardust}")
    private String siteName;

    @Value("${site.description:a DIY Stardust forum}")
    private String siteDescription;

    private User getCurrentUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }

        return (User) auth.getPrincipal();
    }

    private void addCommonAttributes(Model model, Authentication auth) {
        model.addAttribute("siteName", siteName);
        model.addAttribute("siteDescription", siteDescription);

        if (auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal())) {

            model.addAttribute("currentUser", auth.getName());
            model.addAttribute("isLoggedIn", true);

        } else {
            model.addAttribute("isLoggedIn", false);
        }
    }

    @GetMapping("/")
    public String index(Model model, Authentication auth) {

        addCommonAttributes(model, auth);

        List<Subforum> topLevel = subforumRepository.findByParentIsNull();

        model.addAttribute("subforums", topLevel);

        return "subforums";
    }

    @GetMapping("/subforum")
    public String subforum(
            @RequestParam Long sub,
            Model model,
            Authentication auth) {

        addCommonAttributes(model, auth);

        Optional<Subforum> opt = subforumRepository.findById(sub);

        if (opt.isEmpty()) {
            return "redirect:/";
        }

        Subforum sf = opt.get();

        List<Post> posts = postRepository.findBySubforumOrderByPostdateDesc(sf);

        List<Subforum> children = subforumRepository.findByParent(sf);

        String breadcrumb = forumService.generateLinkPath(sub);

        model.addAttribute("subforum", sf);
        model.addAttribute("posts", posts);
        model.addAttribute("children", children);
        model.addAttribute("breadcrumb", breadcrumb);

        return "subforum";
    }

    @GetMapping("/loginform")
    public String loginForm(
            Model model,
            Authentication auth,
            @RequestParam(required = false) String error) {

        addCommonAttributes(model, auth);

        model.addAttribute("errors", new ArrayList<>());

        if (error != null) {

            List<String> errors = new ArrayList<>();

            errors.add("Invalid username or password.");

            model.addAttribute("errors", errors);
        }

        return "login";
    }

    @PostMapping("/action_createaccount")
    public String createAccount(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            Model model,
            Authentication auth) {

        addCommonAttributes(model, auth);

        List<String> errors = new ArrayList<>();

        if (!forumService.validUsername(username)) {
            errors.add(
                    "Username must be 4-40 alphanumeric characters " +
                            "(also allowed: !@#%&).");
        }

        if (!forumService.validPassword(password)) {
            errors.add(
                    "Password must be 6-40 alphanumeric characters " +
                            "(also allowed: !@#%&).");
        }

        if (forumService.usernameTaken(username)) {
            errors.add("Username is already taken.");
        }

        if (forumService.emailTaken(email)) {
            errors.add("Email is already registered.");
        }

        if (!errors.isEmpty()) {

            model.addAttribute("errors", errors);

            return "login";
        }

        User user = new User(
                email,
                username,
                password,
                passwordEncoder);

        userRepository.save(user);

        return "redirect:/loginform";
    }

    @GetMapping("/profile")
    public String profile(
            Model model,
            Authentication auth) {

        addCommonAttributes(model, auth);

        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/loginform";
        }

        User user = getCurrentUser(auth);

        List<Post> recentPosts = postRepository.findTop5ByUserOrderByPostdateDesc(user);

        model.addAttribute("profileUser", user);
        model.addAttribute("recentPosts", recentPosts);

        return "profile";
    }

    @GetMapping("/addpost")
    public String addPostForm(
            @RequestParam Long sub,
            Model model,
            Authentication auth) {

        addCommonAttributes(model, auth);

        Optional<Subforum> opt = subforumRepository.findById(sub);

        if (opt.isEmpty()) {
            return "redirect:/";
        }

        model.addAttribute("subforum", opt.get());
        model.addAttribute("errors", new ArrayList<>());

        return "createpost";
    }

    @PostMapping("/action_post")
    public String createPost(
            @RequestParam Long sub,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) String mediaUrl,
            Model model,
            Authentication auth) {

        addCommonAttributes(model, auth);

        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/loginform";
        }

        List<String> errors = new ArrayList<>();

        if (!forumService.validTitle(title)) {
            errors.add(
                    "Title must be between 5 and 139 characters.");
        }

        if (!forumService.validContent(content)) {
            errors.add(
                    "Content must be between 11 and 4999 characters.");
        }

        Optional<Subforum> opt = subforumRepository.findById(sub);

        if (opt.isEmpty()) {
            return "redirect:/";
        }

        if (!errors.isEmpty()) {

            model.addAttribute("subforum", opt.get());
            model.addAttribute("errors", errors);

            return "createpost";
        }

        User user = getCurrentUser(auth);

        Post post = new Post(
                title,
                content,
                user,
                opt.get());

        if (mediaUrl != null && !mediaUrl.isBlank()) {
            post.setMediaUrl(mediaUrl.trim());
        }

        postRepository.save(post);

        return "redirect:/subforum?sub=" + sub;
    }

    @GetMapping("/viewpost")
    public String viewPost(
            @RequestParam Long post,
            Model model,
            Authentication auth) {

        addCommonAttributes(model, auth);

        Optional<Post> opt = postRepository.findById(post);

        if (opt.isEmpty()) {
            return "redirect:/";
        }

        Post p = opt.get();

        List<Comment> comments = commentRepository.findByPostOrderByPostdateAsc(p);

        String breadcrumb = forumService.generateLinkPath(
                p.getSubforum().getId());

        model.addAttribute("post", p);
        model.addAttribute("comments", comments);
        model.addAttribute("breadcrumb", breadcrumb);
        model.addAttribute("errors", new ArrayList<>());

        User currentUser = getCurrentUser(auth);

        model.addAttribute(
                "likeCount",
                postLikeService.getLikeCount(p));

        model.addAttribute(
                "hasLiked",
                postLikeService.hasLiked(currentUser, p));

        return "viewpost";
    }

    // =========================
    // EDIT POST
    // =========================

    @GetMapping("/editpost")
    public String editPostForm(
            @RequestParam Long post,
            Model model,
            Authentication auth) {

        addCommonAttributes(model, auth);

        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/loginform";
        }

        Optional<Post> opt = postRepository.findById(post);

        if (opt.isEmpty()) {
            return "redirect:/";
        }

        Post p = opt.get();
        User currentUser = getCurrentUser(auth);

        if (currentUser == null ||
                !currentUser.getId().equals(p.getUser().getId())) {

            return "redirect:/viewpost?post=" + post;
        }

        model.addAttribute("post", p);
        model.addAttribute("errors", new ArrayList<>());

        return "editpost";
    }

    @PostMapping("/action_editpost")
    public String editPost(
            @RequestParam Long post,
            @RequestParam String title,
            @RequestParam String content,
            Model model,
            Authentication auth) {

        addCommonAttributes(model, auth);

        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/loginform";
        }

        Optional<Post> opt = postRepository.findById(post);

        if (opt.isEmpty()) {
            return "redirect:/";
        }

        Post p = opt.get();
        User currentUser = getCurrentUser(auth);

        if (currentUser == null ||
                !currentUser.getId().equals(p.getUser().getId())) {

            return "redirect:/viewpost?post=" + post;
        }

        List<String> errors = new ArrayList<>();

        if (!forumService.validTitle(title)) {
            errors.add(
                    "Title must be between 5 and 139 characters.");
        }

        if (!forumService.validContent(content)) {
            errors.add(
                    "Content must be between 11 and 4999 characters.");
        }

        if (!errors.isEmpty()) {

            model.addAttribute("post", p);
            model.addAttribute("errors", errors);

            return "editpost";
        }

        p.setTitle(title);
        p.setContent(content);

        postRepository.save(p);

        return "redirect:/viewpost?post=" + post;
    }

    // =========================
    // DELETE POST
    // =========================

    @PostMapping("/action_deletepost")
    public String deletePost(
            @RequestParam Long post,
            Authentication auth) {

        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/loginform";
        }

        Optional<Post> opt = postRepository.findById(post);

        if (opt.isEmpty()) {
            return "redirect:/";
        }

        Post p = opt.get();
        User currentUser = getCurrentUser(auth);

        if (currentUser == null ||
                !currentUser.getId().equals(p.getUser().getId())) {

            return "redirect:/viewpost?post=" + post;
        }

        Long subforumId = p.getSubforum().getId();

        postRepository.delete(p);

        return "redirect:/subforum?sub=" + subforumId;
    }

    // =========================
    // PIN / UNPIN POST
    // =========================

    @PostMapping("/action_pinpost")
    public String togglePin(
            @RequestParam Long post,
            Authentication auth) {

        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/loginform";
        }

        Optional<Post> opt = postRepository.findById(post);

        if (opt.isEmpty()) {
            return "redirect:/";
        }

        Post p = opt.get();
        User currentUser = getCurrentUser(auth);

        if (currentUser == null ||
                !currentUser.isAdmin()) {

            return "redirect:/viewpost?post=" + post;
        }

        p.setPinned(!p.isPinned());

        postRepository.save(p);

        return "redirect:/viewpost?post=" + post;
    }

    // =========================
    // LIKE / UNLIKE
    // =========================

    @PostMapping("/action_like")
    public String toggleLike(
            @RequestParam Long post,
            Authentication auth) {

        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/loginform";
        }

        Optional<Post> opt = postRepository.findById(post);

        if (opt.isEmpty()) {
            return "redirect:/";
        }

        User user = getCurrentUser(auth);

        postLikeService.toggleLike(
                user,
                opt.get());

        return "redirect:/viewpost?post=" + post;
    }

    // =========================
    // COMMENTS
    // =========================

    @PostMapping("/action_comment")
    public String addComment(
            @RequestParam Long post,
            @RequestParam String content,
            Authentication auth) {

        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/loginform";
        }

        Optional<Post> opt = postRepository.findById(post);

        if (opt.isEmpty()) {
            return "redirect:/";
        }

        User user = getCurrentUser(auth);

        Comment comment = new Comment(
                content,
                user,
                opt.get());

        commentRepository.save(comment);

        return "redirect:/viewpost?post=" + post;
    }

    @GetMapping("/action_comment")
    public String addCommentGet(
            @RequestParam Long post) {

        return "redirect:/viewpost?post=" + post;
    }
}
