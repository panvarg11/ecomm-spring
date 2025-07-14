package com.mpantoja.sbecommerce.service;

import com.mpantoja.sbecommerce.exceptions.APIException;
import com.mpantoja.sbecommerce.exceptions.ResourceNotFoundException;
import com.mpantoja.sbecommerce.model.Cart;
import com.mpantoja.sbecommerce.model.CartItem;
import com.mpantoja.sbecommerce.model.Product;
import com.mpantoja.sbecommerce.payload.CartDTO;
import com.mpantoja.sbecommerce.payload.ProductDTO;
import com.mpantoja.sbecommerce.repositories.CartItemRepository;
import com.mpantoja.sbecommerce.repositories.CartRepository;
import com.mpantoja.sbecommerce.repositories.ProductRepository;
import com.mpantoja.sbecommerce.utils.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthUtil authUtil;


    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Cart cart = createCart();
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);
        if (cartItem!=null) {
            throw new APIException("Product " + product.getProductName() + " already exists in the cart");
        }
        if (product.getQuantity()==0) {
            throw new APIException(product.getProductName() + " is out of stock");
        }

        if (product.getQuantity() < quantity) {
            throw new APIException("For" + product.getProductName() + "\nThere are currently less products than requested in stock. Quantity available: " + product.getQuantity());
        }

        CartItem newCartItem = new CartItem();
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProdudctPrice(product.getFinalPrice());
        newCartItem.setProduct(product);

        cartItemRepository.save(newCartItem);
//        product.setQuantity(product.getQuantity()-quantity);
        cart.setTotalPrice(cart.getTotalPrice() + (product.getFinalPrice() * quantity));

        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productDTOStream = cartItems.stream().map(item -> {
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setQuantity(item.getQuantity());
            return map;
        });

        cartDTO.setProducts(productDTOStream.toList());
        return cartDTO;

    }

    @Override
    public List<CartDTO> getAllCarts() {

        List<Cart> carts = cartRepository.findAll();
        if (carts.isEmpty()) {
            throw new APIException("No carts found");
        }
//TODO
//        List<CartDTO> cartDTOS = carts.stream().map(cart -> modelMapper.map(cart, CartDTO.class)).toList();

        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            List<ProductDTO> productDTOS = cart.getCartItems().stream().map(prod ->
                    modelMapper.map(prod.getProduct(), ProductDTO.class)).toList();
            cartDTO.setProducts(productDTOS);
            return cartDTO;
        }).toList();

        return cartDTOs;
    }

    @Override
    public CartDTO gerCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
        if (cart==null) {
            throw new ResourceNotFoundException("cart", "cartId", cartId);
        }
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        cart.getCartItems().forEach(c -> c.getProduct().setQuantity(c.getQuantity()));
        List<ProductDTO> products = cart.getCartItems().stream().map(prod -> modelMapper.map(prod.getProduct(), ProductDTO.class)).toList();
        cartDTO.setProducts(products);
        return cartDTO;
    }

    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {

        String emailId = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(emailId);
        Long cartId = userCart.getCartId();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "Cart ID", cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        if (product.getQuantity()==0) {
            throw new APIException(product.getProductName() + " is out of stock");
        }

        if (product.getQuantity() < quantity) {
            throw new APIException("For" + product.getProductName() + "\nThere are currently less products than requested in stock. Quantity available: " + product.getQuantity());
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if(cartItem==null){
            throw new APIException("Product "+product.getProductName()+" Not available in cart");
        }

        int newQuantity = cartItem.getQuantity()+quantity;
        if(newQuantity>0){
            throw new APIException("Resulting quantity cannot be negative");
        }

        if(newQuantity==0){
            deleteProductFromCart(cartId,productId);
        }else{
            cartItem.setProdudctPrice(product.getFinalPrice());
            cartItem.setQuantity(cartItem.getQuantity()+quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice()+(cartItem.getProdudctPrice()*quantity));
            cartRepository.save(cart);
        }

        CartItem updateItem = cartItemRepository.save(cartItem);
        if(updateItem.getQuantity()==0){
            cartItemRepository.deleteById(updateItem.getCartItemId());
        }

        CartDTO cartDto = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productDTOStream = cartItems.stream().map(item->{
            ProductDTO prd = modelMapper.map(item.getProduct(), ProductDTO.class);
            prd.setQuantity(item.getQuantity());
            return prd;
        });

        cartDto.setProducts(productDTOStream.toList());

        return cartDto;
    }

    @Override
    @Transactional
    public String deleteProductFromCart(Long cartId, Long productId) {

        Cart cart = cartRepository.findById(cartId).orElseThrow(()-> new ResourceNotFoundException("Cart", "cartID", cartId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if(cartItem==null){
            throw new ResourceNotFoundException("Product", "Product Id", productId);
        }

        cart.setTotalPrice(cart.getTotalPrice()-(cartItem.getProdudctPrice()*cartItem.getQuantity()));

        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);
        return "Product "+cartItem.getProduct().getProductName()+ " Removed from Cart";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "Cart ID", cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if(cartItem==null){
            throw new APIException("Product "+product.getProductName()+" not Available in the cart");

        }
        double cartPrice=cart.getTotalPrice()-(cartItem.getProdudctPrice()*cartItem.getQuantity());

        cartItem.setProdudctPrice(product.getFinalPrice());

        cart.setTotalPrice(cartPrice+
                (cartItem.getProdudctPrice()*cartItem.getQuantity()));

        cartItem = cartItemRepository.save(cartItem);

    }

    private Cart createCart() {

        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if (userCart!=null) {
            return userCart;
        }

        Cart cart = cart();
        cart.setUser(authUtil.loggedInUser());
        cart.setTotalPrice(0.0);
        return cartRepository.save(cart);
    }

    private Cart cart() {
        return new Cart();
    }

}
