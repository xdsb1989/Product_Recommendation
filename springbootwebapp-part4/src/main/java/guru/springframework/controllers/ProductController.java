package guru.springframework.controllers;

import guru.springframework.bootstrap.FileArchiveService;
import guru.springframework.domain.CustomerImage;
import guru.springframework.domain.Product;
import guru.springframework.repositories.ProductRepository;
import guru.springframework.services.ProductService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;



@Controller
public class ProductController {

    private ProductService productService;
    
    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Autowired
	private FileArchiveService fileArchiveService;
    
    @RequestMapping(value = "/products", method = RequestMethod.GET)
    public String list(Model model){
        model.addAttribute("products", productService.listAllProducts());
        
        return "products";
    }

    @RequestMapping("product/{id}")
    public String showProduct(@PathVariable Integer id, Model model){
    	Product product = productService.getProductById(id);
    	int click = product.getClickrate();
    	click++;
    	product.setClickrate(click);
    	productService.saveProduct(product);
        model.addAttribute("product", product);
        return "productshow";
    }

    @RequestMapping("product/edit/{id}")
    public String edit(@PathVariable Integer id, Model model){
        model.addAttribute("product", productService.getProductById(id));
        return "productform";
    }

    @RequestMapping("product/new")
    public String newProduct(Model model){
        model.addAttribute("product", new Product());
        return "productform";
    }

    @RequestMapping(value = "product", method = RequestMethod.POST)
    public String saveProduct(Product product, @RequestParam("thisimage") MultipartFile image) throws Exception{
    	
    	CustomerImage customerImage = fileArchiveService.saveFileToS3(image);
    	product.setCustomerImage(customerImage);
    	
        productService.saveProduct(product);
        return "redirect:/product/" + product.getId();
    }

    @RequestMapping("product/delete/{id}")
    public String delete(@PathVariable Integer id){
    	Product product = productService.getProductById(id);
    	fileArchiveService.deleteImageFromS3(product.getCustomerImage());
        productService.deleteProduct(id);
        return "redirect:/products";
    }
    
    @Autowired
	private QueryParser translater;
    
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String searchlist(Model model, @RequestParam("content") String content){
    	List<String> word_list = translater.parseQuery(content);
    	HashSet<String> keywords_set = new HashSet<String>();
    	for (int i=0; i<word_list.size(); i++){
    		keywords_set.add(word_list.get(i));
    	}
    	
    	List<Product> products_list = (List<Product>) productService.listAllProducts();
    	List<Product> candidates = new ArrayList<Product>();
    	
    	for (int i=0; i<products_list.size(); i++){
    		if (keywords_set.contains(products_list.get(i).getProductId())){
    			candidates.add(products_list.get(i));
    			model.addAttribute("products", candidates);
    	        return "products";
    		}
    	}
    	
    	
    	for (int i=0; i<products_list.size(); i++){
    		String temp[] = products_list.get(i).getDescription().split("[^a-zA-Z0-9']+");
    		int count = 0;
    		for (String str : temp){
    			if (keywords_set.contains(str)){
    				count++;
    			}
    		}
    		if (count > 0){
    			products_list.get(i).setKeywords_number(count);
    			candidates.add(products_list.get(i));
    		}
    	}
    	
    	Collections.sort(candidates, new SortingMethod());
    	model.addAttribute("products", candidates);
        return "products";
    }
    
    class SortingMethod implements Comparator<Product> {

		@Override
		public int compare(Product product1, Product product2) {
			double score1 = product1.getClickrate()*0.5 + product1.getKeywords_number()*10;
			double score2 = product2.getClickrate()*0.5 + product2.getKeywords_number()*10;
			double price1 = product1.getPrice();
			double price2 = product2.getPrice();
			if (score1 > score2) {
				return -1;
			}
			else if (score1 < score2) {
				return 1;
			}
			else {
				if (price1 < price2) {
					return -1;
				}
				else {
					return 1;
				}
			}
		}
    	
    }
}
